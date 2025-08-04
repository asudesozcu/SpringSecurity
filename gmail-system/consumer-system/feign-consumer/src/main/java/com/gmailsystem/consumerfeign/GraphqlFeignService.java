package com.gmailsystem.consumerfeign;

import com.gmailsystem.consumerfeign.client.GmailApiClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GraphqlFeignService {

    private final GmailApiClient graphqlFeignClient;

    public GraphqlFeignService(GmailApiClient graphqlFeignClient) {
        this.graphqlFeignClient = graphqlFeignClient;
    }

    public String getEmails(String protocol, List<String> fields) {
        String selectedFields = String.join(" ", fields);

        String query = String.format("""
            query {
              getEmails(protocol: %s) {
                %s
              }
            }
        """, protocol, selectedFields);

        Map<String, Object> body = Map.of("query", query);

        return graphqlFeignClient.executeGraphql(body);
    }
}