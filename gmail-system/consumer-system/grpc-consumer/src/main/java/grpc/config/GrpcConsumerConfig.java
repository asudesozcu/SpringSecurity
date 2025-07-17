package grpc.config;


import common.EmailServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class GrpcConsumerConfig {

    @Value("${grpc.server.address}")
    private String grpcServerAddress;

    @Value("${grpc.server.port}")
    private int grpcServerPort;

    @Bean
    public ManagedChannel grpcChannel() {
        return ManagedChannelBuilder.forAddress(grpcServerAddress, grpcServerPort)
                .usePlaintext()
                .build();
    }

    @Bean
    public EmailServiceGrpc.EmailServiceBlockingStub emailServiceBlockingStub(ManagedChannel channel) {
        return EmailServiceGrpc.newBlockingStub(channel);
    }
}
