package com.gmailsystem.googlelogin.service;

import common.EmailServiceGrpc;
import common.EmailRequest;
import common.EmailResponse;
import com.gmailsystem.dto.EmailDto;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import com.gmailsystem.googlelogin.model.User;
import com.gmailsystem.googlelogin.repo.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@GrpcService
public class GrpcProducerService extends EmailServiceGrpc.EmailServiceImplBase {

    private final GmailService gmailService;
    private final UserRepository userRepository;

    public GrpcProducerService(GmailService gmailService, UserRepository userRepository) {
        this.gmailService = gmailService;
        this.userRepository = userRepository;
    }

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    private final Map<String, Instant> lastRequestTime = new ConcurrentHashMap<>();
    private static final long RATE_LIMIT_SECONDS = 10;

    @Override
    public void fetchLatestEmails(EmailRequest request, StreamObserver<EmailResponse> responseObserver) {
        User user = BearerTokenInterceptor.USER_CTX_KEY.get();

        if (user == null) {
            responseObserver.onError(Status.UNAUTHENTICATED.withDescription("User context missing").asRuntimeException());
            return;
        }

        String email = user.getEmail();
//        Instant now = Instant.now();
//        Instant last = lastRequestTime.get(email);

        Mono<List<String>> emailMono;

//        if (last != null && now.isBefore(last.plusSeconds(RATE_LIMIT_SECONDS))) {
//            emailMono = Mono.delay(Duration.ofSeconds(5))
//                    .then(gmailService.fetchLatestMails(user, clientId, clientSecret));
//        } else {
//            lastRequestTime.put(email, now);
            emailMono = gmailService.fetchLatestMails(user, clientId, clientSecret);
      //  }


        emailMono.subscribe(
                emails -> {
// Raw String -> dto.EmailDto
                    List<EmailDto> dtoList = emails.stream()
                            .map(gmailService::parseEmailString)
                            .collect(Collectors.toList());
//dto.EmailDto-> common.EmailDto (proto)
                    List<common.EmailDtoProto> protoList = dtoList.stream()
                            .map(EmailDto::toProto)
                            .collect(Collectors.toList());

                    EmailResponse response = EmailResponse.newBuilder()
                            .addAllFetchedEmails(protoList)
                            .build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                },
                error -> {
                    responseObserver.onError(Status.INTERNAL.withDescription(error.getMessage()).asRuntimeException());
                }
        );
    }

}
