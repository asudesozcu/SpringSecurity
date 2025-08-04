package com.gmailsystem.consumerfeign.controller;

import com.gmailsystem.consumerfeign.GraphqlFeignService;
import com.gmailsystem.consumerfeign.client.GmailApiClient;
import graphql.kickstart.execution.GraphQLRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.gmailsystem.dto.EmailDto;
@RestController
@RequestMapping("/feign-graphql")
public class GmailConsumerController {

    private final GraphqlFeignService graphqlFeignService;

    public GmailConsumerController(GraphqlFeignService graphqlFeignService) {
        this.graphqlFeignService = graphqlFeignService;
    }
    @PostMapping
    public String callGraphql(@RequestBody Map<String, Object> request) {
        String protocol = (String) request.get("protocol");
        List<String> fields = (List<String>) request.get("fields");

        return graphqlFeignService.getEmails(protocol, fields);
    }

}