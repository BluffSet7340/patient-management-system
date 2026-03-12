package com.pm.patient_service.grpc;

import java.util.concurrent.ForkJoinPool.ManagedBlocker;

import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BilingSeviceGrpcClient {
    // nested class within billingservicegrpc class providing synchronous client
    // calls to the grpc server running in the billing service - execution waits for
    // respose from server before continuing
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    private final Logger log = org.slf4j.LoggerFactory.getLogger(BilingSeviceGrpcClient.class);

    // localhost:9001/BillingService/CreatePatientAccount
    // has to be changed once deployed to production env
    public BillingServiceGrpcClient(
        @Value("${billing.service.address:localhost}") String serverAddress, @Value("${billing.service.grpc.port:9001}") int serverPort
    ){
        log.info("Connecting to billing services at Ip address {} and port {}", serverAddress, serverPort);

    // //     // takes serveraddr and server post and creates a managed channel 
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext().build();

        // client has been created now, runs code and starts client for us behind the scenes
        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

}
