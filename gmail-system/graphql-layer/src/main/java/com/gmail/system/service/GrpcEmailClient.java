package com.gmail.system.service;

import common.EmailRequest;
import common.EmailResponse;
import common.EmailServiceGrpc;
import dto.EmailDto;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class GrpcEmailClient {

    private final EmailServiceGrpc.EmailServiceBlockingStub stub;

    public GrpcEmailClient(EmailServiceGrpc.EmailServiceBlockingStub stub) {
        this.stub = stub;
    }

    public List<EmailDto> fetchEmails(String token) {
        // gRPC Metadata içine Authorization header'ı ekliyoruz
        Metadata metadata = new Metadata();
        metadata.put(
                Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                token
        );
        System.out.println("metadata: " + metadata);

        ClientInterceptor interceptor = MetadataUtils.newAttachHeadersInterceptor(metadata);

        EmailServiceGrpc.EmailServiceBlockingStub stubWithAuth =
                stub.withInterceptors(interceptor);

        // Boş bir request gönderiyoruz çünkü .proto'da EmailRequest boş
        EmailRequest request = EmailRequest.newBuilder().build();

        EmailResponse response = stubWithAuth.fetchLatestEmails(request);

        return response.getFetchedEmailsList().stream()
                .map(EmailDto::fromProto)
                .collect(Collectors.toList());
    }
}
