package com.gmailsystem.graphql.graphql.service.grpcconsumer;

import com.gmailsystem.dto.EmailDto;
import common.EmailRequest;
import common.EmailResponse;
import common.EmailServiceGrpc;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.springframework.cloud.commons.security.ResourceServerTokenRelayAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GrpcEmailClientService {


        private final EmailServiceGrpc.EmailServiceBlockingStub stub;

        public GrpcEmailClientService(EmailServiceGrpc.EmailServiceBlockingStub stub) {
            this.stub = stub;
        }

        public List<EmailDto> fetchEmails(String token) {
            Metadata metadata = new Metadata();
            metadata.put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer " + token
            );
            System.out.println("GRPC EMAİL CLİENT SERVİCE:" +token);
            // Interceptor oluştur ve stub'a ekle
            ClientInterceptor interceptor = MetadataUtils.newAttachHeadersInterceptor(metadata);

            EmailServiceGrpc.EmailServiceBlockingStub stubWithAuth =
                    stub.withInterceptors(interceptor); // zaten injected edilmiş stub'ı genişletiyoruz

            EmailRequest request = EmailRequest.newBuilder().build();
            EmailResponse response = stubWithAuth.fetchLatestEmails(request);

            return response.getFetchedEmailsList().stream().map(EmailDto::fromProto).collect(Collectors.toList());
        }

    }

