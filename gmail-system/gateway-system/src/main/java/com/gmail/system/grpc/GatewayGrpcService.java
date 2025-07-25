package com.gmail.system.grpc;

import common.EmailRequest;
import common.EmailResponse;
import common.EmailServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class GatewayGrpcService extends EmailServiceGrpc.EmailServiceImplBase {

    @GrpcClient("emailService") // burada gerçek gRPC sunucuya bağlanılır
    private EmailServiceGrpc.EmailServiceBlockingStub realStub;

    @Override
    public void fetchLatestEmails(EmailRequest request, StreamObserver<EmailResponse> responseObserver) {
        EmailResponse realResponse = realStub.fetchLatestEmails(request);
        responseObserver.onNext(realResponse);
        responseObserver.onCompleted();
    }










}
