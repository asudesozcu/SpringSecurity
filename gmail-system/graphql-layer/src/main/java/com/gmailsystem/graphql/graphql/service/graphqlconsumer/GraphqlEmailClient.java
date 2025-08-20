package com.gmailsystem.graphql.graphql.service.graphqlconsumer;

import com.gmailsystem.dto.EmailDto;
import com.gmailsystem.graphql.graphql.dto.EmailData;
import com.gmailsystem.graphql.graphql.dto.GraphqlFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@FeignClient(name = "graphql-server", url = "http://localhost:8086")
public interface GraphqlEmailClient {

    @PostMapping(value = "/graphql", consumes = "application/json")
    Map<String, Object> getEmails(@RequestBody String queryJson,
                                  @RequestHeader("Cookie") String cookie);
}