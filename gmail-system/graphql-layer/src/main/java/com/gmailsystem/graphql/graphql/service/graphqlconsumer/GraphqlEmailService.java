package com.gmailsystem.graphql.graphql.service.graphqlconsumer;

import com.gmailsystem.dto.EmailDto;
import com.gmailsystem.graphql.graphql.dto.EmailData;
import com.gmailsystem.graphql.graphql.dto.GraphqlFeignResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GraphqlEmailService {

    private final GraphqlEmailClient feignClient;

    public GraphqlEmailService(GraphqlEmailClient feignClient) {
        this.feignClient = feignClient;
    }

    public Map<String, Object> fetchEmailsDynamic(String cookie, String fields) {
        String query = String.format("{ \"query\": \"query { getEmails { %s } }\" }", fields);

        Map<String, Object> rawResponse = feignClient.getEmails(query, cookie);

        Map<String, Object> data = (Map<String, Object>) rawResponse.get("data");
        List<Map<String, Object>> emails = (List<Map<String, Object>>) data.get("getEmails");

        Set<String> allowedFields = Arrays.stream(fields.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        List<Map<String, Object>> filteredEmails = emails.stream()
                .map(email -> email.entrySet().stream()
                        .filter(entry -> allowedFields.contains(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .collect(Collectors.toList());

        return Map.of("getEmails", filteredEmails);
    }
    }

