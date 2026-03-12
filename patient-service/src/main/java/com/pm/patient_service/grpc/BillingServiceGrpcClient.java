package com.pm.patient_service.grpc;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Service
public class BillingServiceGrpcClient {
    // nested class within billingservicegrpc class providing synchronous client
    // calls to the grpc server running in the billing service - execution waits for
    // respose from server before continuing
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;
    private final Logger log = org.slf4j.LoggerFactory.getLogger(BillingServiceGrpcClient.class);

    // localhost:9001/BillingService/CreatePatientAccount
    // has to be changed once deployed to production env
    public BillingServiceGrpcClient(
        @Value("${billing.service.address:localhost}") String serverAddress, @Value("${billing.service.grpc.port:9001}") int serverPort
    ){
        log.info("Connecting to billing services at Ip address {} and port {}", serverAddress, serverPort);

    // //     // takes serveraddr and server post and creates a managed channel 
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).
        usePlaintext().build();

        // client has been created now, runs code and starts client for us behind the scenes
        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    // the setter are generated for us by the builder
    public BillingResponse createBillingAccount(String patientId, String name, String email){
        BillingRequest request = BillingRequest.newBuilder().setPatientId(patientId).setEmail(email).setName(name).build();

        // same as running the grpc request manually via postman
        BillingResponse response = blockingStub.createBillingAccount(request);

        log.info("Received response from billing service via grpc: {}", response);

        return response;
    }
}
