package grpc.service;

import dto.EmailDto;
import common.EmailServiceGrpc;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import common.EmailRequest;
import common.EmailResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GrpcConsumerService {

    private final EmailServiceGrpc.EmailServiceBlockingStub stub;

    public GrpcConsumerService(EmailServiceGrpc.EmailServiceBlockingStub stub) {
        this.stub = stub;
    }

    public List<EmailDto> fetchEmails(String token) {
        Metadata metadata = new Metadata();
        metadata.put(
                Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                "Bearer " + token
        );

        // Interceptor oluştur ve stub'a ekle
        ClientInterceptor interceptor = MetadataUtils.newAttachHeadersInterceptor(metadata);

        EmailServiceGrpc.EmailServiceBlockingStub stubWithAuth =
                stub.withInterceptors(interceptor); // zaten injected edilmiş stub'ı genişletiyoruz

        EmailRequest request = EmailRequest.newBuilder().build();
        EmailResponse response = stubWithAuth.fetchLatestEmails(request);

        return response.getFetchedEmailsList().stream().map(dto.EmailDto::fromProto).collect(Collectors.toList());
    }

}