package com.pm.stack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Token;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.ec2.ISubnet;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.CloudMapNamespaceOptions;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerDefinitionOptions;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.FargateService;
import software.amazon.awscdk.services.ecs.FargateTaskDefinition;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.PortMapping;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.rds.Credentials;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.amazon.awscdk.services.rds.PostgresEngineVersion;
import software.amazon.awscdk.services.rds.PostgresInstanceEngineProps;
import software.amazon.awscdk.services.route53.CfnHealthCheck;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;

public class LocalStack extends Stack {
        private final Vpc vpc;
        private final Cluster ecsCluster;

        // boilerplate code
        // id is identifier of stack, props are additional parameters we want to add
        public LocalStack(final App scope, final String id, final StackProps props) {
                super(scope, id, props);

                // createVpc needs to be defined
                this.vpc = createVpc();

                // define the db in your constructor
                DatabaseInstance authServiceDb = createDatabase("AuthServiceDb", "auth-service-db");
                DatabaseInstance patientServiceDb = createDatabase("PatientServiceDb", "patient-service-db");

                CfnHealthCheck authDbHealthCheck = CfnHealthCheckForDb(authServiceDb, "AuthServiceDbHealthCheck");
                CfnHealthCheck patientDbHealthCheck = CfnHealthCheckForDb(patientServiceDb,
                                "PatientServiceDbHealthCheck");

                CfnCluster mskCluster = CfnClusterKafka();

                this.ecsCluster = createEcsCluster();

                FargateService authService = createFargateService("AuthService", "auth-service", List.of(4005),
                                authServiceDb, Map.of("JWT_SECRET", "${jwt.secret}"));

                // adding health check and database as dependencies
                authService.getNode().addDependency(authDbHealthCheck);
                authService.getNode().addDependency(authServiceDb);

                FargateService billingService = createFargateService("BillingService", "billing-service",
                                List.of(5001, 9001), null, null);

                FargateService analyticsService = createFargateService("AnalyticsService", "analytics-service",
                                List.of(5002), null, null);

                analyticsService.getNode().addDependency(mskCluster);

                FargateService patientService = createFargateService("PatientService", "patient-service",
                                // localstack does not have tbe cloud discovery so we use the internal docker
                                // network, and the its associated port to access it
                                List.of(5002), patientServiceDb,
                                Map.of("BILLING_SERVICE_ADDRESS", "host.docker.internal",
                                                "BILLING_SERVICE_GRPC_PORT", "9001"));
                // need these dependencies to make it work
                patientService.getNode().addDependency(patientServiceDb);
                patientService.getNode().addDependency(patientDbHealthCheck);
                patientService.getNode().addDependency(billingService);


                createApiGatewayService();
        }

        private Vpc createVpc() {
                // references the LocalStack class
                return Vpc.Builder
                                // "this" refers to the LocalStack class definition up top
                                .create(this, "PatientManagementVPC")
                                .vpcName("PatientManagementVPC")
                                .maxAzs(2) // max number of available zones - 2 zone throughout the world
                                .build();
        }

        // create two dbs - one for patients and the other for authentication
        private DatabaseInstance createDatabase(String id, String dbName) {
                return DatabaseInstance.Builder
                                .create(this, id)
                                .engine(DatabaseInstanceEngine.postgres(
                                                PostgresInstanceEngineProps.builder()
                                                                .version(PostgresEngineVersion.VER_17_2).build()))
                                .vpc(vpc) // vpc instance already defined
                                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO)) // set the
                                                                                                             // compute
                                                                                                             // power
                                                                                                             // that
                                                                                                             // your
                                                                                                             // database
                                                                                                             // needs
                                .allocatedStorage(20) // 20gb specified
                                .credentials(Credentials.fromGeneratedSecret("admin")) // generate secret from this
                                                                                       // username
                                .databaseName(dbName)
                                .removalPolicy(RemovalPolicy.DESTROY) // stack is destroyed db storage is also gone
                                .build();
        }

        private CfnHealthCheck CfnHealthCheckForDb(DatabaseInstance databaseInstance, String id) {
                return CfnHealthCheck.Builder.create(this, id)
                                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                                                .type("TCP")
                                                .port(Token.asNumber(databaseInstance.getDbInstanceEndpointPort()))
                                                .ipAddress(Token.asString(
                                                                databaseInstance.getDbInstanceEndpointAddress()))
                                                .requestInterval(30) // check endpoint every 30 seconds => gives db a
                                                                     // chance to startup
                                                .failureThreshold(3) // performs 3 tries before reporting a failure
                                                .build())
                                .build();
        }

        private CfnCluster CfnClusterKafka() {
                return CfnCluster.Builder.create(this, "MskCluster")
                                .clusterName("kafka-cluster-for-patients")
                                .kafkaVersion("4.2.0")
                                .numberOfBrokerNodes(1) // connects vpn to broker node
                                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                                                .instanceType("kafka.m5.xlarge") // specifying
                                                                                 // size
                                                                                 // of
                                                                                 // machine
                                                                                 // to
                                                                                 // run
                                                                                 // on
                                                                                 // -
                                                                                 // more
                                                                                 // compute
                                                                                 // power
                                                .clientSubnets(
                                                                vpc.getPrivateSubnets().stream()
                                                                                .map(ISubnet::getSubnetId)
                                                                                .collect(Collectors.toList()))
                                                .brokerAzDistribution("DEFAULT").build())
                                .build();
        }

        // so to find a specific service add the name of the service (container name)
        // and the name of the namespace
        // eg. auth-service.patient-management.local
        private Cluster createEcsCluster() {
                // added the service discovery namespace to this cluster, makes it easy to find
                // this service and read about it
                // does not run well on localstack since it runs on localhost but this works for
                // an actual amazon aws system
                return Cluster.Builder.create(this, "patientManagementCluster").vpc(vpc)
                                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                                                .name("patient-management.local").build())
                                .build();
        }

        // create service using the FargateService launch type, makes it easy to start,
        // stop, scale ecs tasks that run the containers
        // the last argument is a map of key value pairs
        private FargateService createFargateService(String id, String imageName, List<Integer> ports,
                        DatabaseInstance dbName, Map<String, String> addEnvVars) {
                // specify the resources being used for a container
                FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder.create(this, id + "Task")
                                .cpu(256) // 256 cpu units
                                .memoryLimitMiB(512)
                                .build();

                // adding the image name to run the container
                // keeps the containerOptions as Builder instance instead of class instance
                ContainerDefinitionOptions.Builder containerOptions = ContainerDefinitionOptions.builder()
                                .image(ContainerImage.fromRegistry(imageName))
                                .portMappings(ports.stream().map( // port mapping to expose the ports of the container
                                                port -> PortMapping.builder().containerPort(port).hostPort(port)
                                                                .protocol(Protocol.TCP).build())
                                                .toList())
                                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                                .logGroup(LogGroup.Builder.create(this, id + "LogGroup")
                                                                .logGroupName("/ecs/" + imageName)
                                                                .removalPolicy(RemovalPolicy.DESTROY)
                                                                .retention(RetentionDays.ONE_DAY) // keeping logs can be
                                                                                                  // expensive, reduce
                                                                                                  // it
                                                                .build())
                                                .streamPrefix(imageName)
                                                .build()));

                Map<String, String> envVars = new HashMap<>();
                // local addresses where aws can set the kafka servers on - patient service and
                // analytics service can use these
                envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS",
                                "localhost.localstack.cloud:4511, localhost.localstack.cloud:4512");

                if (addEnvVars != null) {
                        envVars.putAll(addEnvVars);
                }

                if (dbName != null) {
                        // the way this works jdbc:postgresql://%s:%s/%s-db => each %s is replaced with
                        // endpoint address, port, and image name
                        envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s-db"
                                        .formatted(dbName.getDbInstanceEndpointAddress(),
                                                        dbName.getDbInstanceEndpointPort(), imageName));

                        envVars.put("SPRING_DATASOURCE_USERNAME", "admin");
                        // secret manager creates a password for you behind the scenes, all you have to
                        // do is to grab it
                        envVars.put("SPRING_DATASOURCE_PASSWORD",
                                        dbName.getSecret().secretValueFromJson("password").toString());
                        // same list of patients to use for testing, for production this should be
                        // changed
                        envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
                        envVars.put("SPRING_SQL_INIT_MODE", "always");
                        envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "60000");
                }

                containerOptions.environment(envVars);

                // next step is to add containerOptions to taskDefinition
                taskDefinition.addContainer(imageName + " Container", containerOptions.build()); // pass the image and
                                                                                                 // build the container

                // also controls external access to a task running in a given container and it
                // can be opened to the public internet but we don't need that
                return FargateService.Builder.create(this, id).cluster(ecsCluster).taskDefinition(taskDefinition)
                                .assignPublicIp(false).serviceName(imageName).build();
        }

        private void createApiGatewayService() {
                                // specify the resources being used for a container
                FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder.create(this, "ApiGatewayTaskDefinition")
                                .cpu(256) // 256 cpu units
                                .memoryLimitMiB(512)
                                .build();

                                // adding the image name to run the container
                // building in one go
                ContainerDefinitionOptions containerOptions = ContainerDefinitionOptions.builder()
                // api-gateway will have different set of routes for production and testing
                                .image(ContainerImage.fromRegistry("api-gateway")).environment(Map.of("SPRING_PROFILES_ACTIVE", "prod", 
                                        "AUTH_SERVICE_URL", "host.docker.internal:4005"
                                ))
                                .portMappings(List.of(5004).stream().map( // port mapping to expose the ports of the container
                                                port -> PortMapping.builder().containerPort(port).hostPort(port)
                                                                .protocol(Protocol.TCP).build())
                                                .toList())
                                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                                .logGroup(LogGroup.Builder.create(this, "ApiGatewayLogGroup")
                                                                .logGroupName("/ecs/api-gateway")
                                                                .removalPolicy(RemovalPolicy.DESTROY)
                                                                .retention(RetentionDays.ONE_DAY) // keeping logs can be
                                                                                                  // expensive, reduce
                                                                                                  // it
                                                                .build())
                                                .streamPrefix("api-gateway")
                                                .build()))
                                                .build();

                taskDefinition.addContainer("APIGatewayContainer", containerOptions);

                // this service class is used to act as the public endpoint, created automatically for us
                ApplicationLoadBalancedFargateService apiGateway = ApplicationLoadBalancedFargateService.Builder.create(this, "APIGatewayService").cluster(ecsCluster) // entry point to our cluster
                .serviceName("api-gateway").taskDefinition(taskDefinition).desiredCount(1).healthCheckGracePeriod(Duration.seconds(60)).build();
        }

        public static void main(final String[] args) {
                // creating a new cdk app and defining where the output should be
                App app = new App(AppProps.builder().outdir("./cdk.out").build());
                // defining props
                StackProps props = StackProps.builder()
                                .synthesizer(new BootstraplessSynthesizer()) // synthesizer is an aws term to convert
                                                                             // JAVA code to a
                                                                             // cloud formation template
                                // boot-strapless to skip the initial bootstrapping of the cdk environment
                                .build();

                new LocalStack(app, "localstack", props);
                app.synth(); // take the stack, add props, and convert to a cloud formation template stored
                             // at the specified folder
                System.out.println("App synthesizing in progress");
        }

}
