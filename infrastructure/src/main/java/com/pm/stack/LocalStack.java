package com.pm.stack;

import com.amazonaws.services.route53.model.VPC;

import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.rds.Credentials;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.amazon.awscdk.services.rds.PostgresEngineVersion;
import software.amazon.awscdk.services.rds.PostgresInstanceEngineProps;

public class LocalStack extends Stack {
    private final Vpc vpc;

    // boilerplate code
    // id is identifier of stack, props are additional parameters we want to add
    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);

        // createVpc needs to be defined
        this.vpc = createVpc();

        // define the db in your constructor
        DatabaseInstance authServiceDb = createDatabase("AuthServiceDb", "auth-service-db");
        DatabaseInstance patientServiceDb = createDatabase("PatientServiceDb", "patient-service-db");
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
