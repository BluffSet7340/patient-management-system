package com.pm.stack;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.route53.model.VPC;

import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Token;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.msk.CfnCluster.BrokerNodeGroupInfoProperty;
import software.amazon.awscdk.services.ec2.ISubnet;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.CloudMapNamespaceOptions;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.FargateService;
import software.amazon.awscdk.services.rds.Credentials;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.amazon.awscdk.services.rds.PostgresEngineVersion;
import software.amazon.awscdk.services.rds.PostgresInstanceEngineProps;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

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
        CfnHealthCheck patientDbHealthCheck = CfnHealthCheckForDb(patientServiceDb, "PatientServiceDbHealthCheck");

        CfnCluster mskCluster = CfnClusterKafka();

        this.ecsCluster = createEcsCluster();
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
                        PostgresInstanceEngineProps.builder().version(PostgresEngineVersion.VER_17_2).build()))
                .vpc(vpc) // vpc instance already defined
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO)) // set the compute power
                                                                                             // that your database needs
                .allocatedStorage(20) // 20gb specified
                .credentials(Credentials.fromGeneratedSecret("admin")) // generate secret from this username
                .databaseName(dbName)
                .removalPolicy(RemovalPolicy.DESTROY) // stack is destroyed db storage is also gone
                .build();
    }

    private CfnHealthCheck CfnHealthCheckForDb(DatabaseInstance databaseInstance, String id) {
        return CfnHealthCheck.Builder.create(this, id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                        .type("TCP")
                        .port(Token.asNumber(databaseInstance.getDbInstanceEndpointPort()))
                        .ipAddress(Token.asString(databaseInstance.getDbInstanceEndpointAddress()))
                        .requestInterval(30) // check endpoint every 30 seconds => gives db a chance to startup
                        .failureThreshold(3) // performs 3 tries before reporting a failure
                        .build())
                .build();
    }

    private CfnCluster CfnClusterKafka() {
        return CfnCluster.Builder.create(this, "MskCluster")
        .clusterName("kafka-cluster-for-patients")
        .kafkaVersion("2.8.0")
        .numberOfBrokerNodes(1) // connects vpn to broker node
        .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder().instanceType("kafka.m5.xlarge") // specifying size of machine to run on - more compute power
        .clientSubnets(vpc.getPrivateSubnets().stream().map(ISubnet::getSubnetId).collect(Collectors.toList()))
        .brokerAzDistribution("DEFAULT").build()).build(); 
    }

    // so to find a specific service add the name of the service (container name) and the name of the namespace
    // eg. auth-service.patient-management.local
    private Cluster createEcsCluster() {
        // added the service discovery namespace to this cluster, makes it easy to find this service and read about it
        // does not run well on localstack since it runs on localhost but this works for an actual amazon aws system
        return Cluster.Builder.create(this, "patientManagementCluster").vpc(vpc).defaultCloudMapNamespace(CloudMapNamespaceOptions.builder().name("patient-management.local").build()).build();
    }

    // create service using the FargateService launch type, makes it easy to start, stop, scale ecs tasks that run the containers
    // the last argument is a map of key value pairs
    // private FargateService creatFargateService(String id, String imageName, List<Integer> ports, DatabaseInstance dbName, Map<String, String> addEnvVars) {

    // }

    public static void main(final String[] args) {
        // creating a new cdk app and defining where the output should be
        App app = new App(AppProps.builder().outdir("./cdk.out").build());
        // defining props
        StackProps props = StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer()) // synthesizer is an aws term to convert JAVA code to a
                                                             // cloud formation template
                // boot-strapless to skip the initial bootstrapping of the cdk environment
                .build();

        new LocalStack(app, "localstack", props);
        app.synth(); // take the stack, add props, and convert to a cloud formation template stored
                     // at the specified folder
        System.out.println("App synthesizing in progress");
    }

}
