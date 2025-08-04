package com.gmailsystem.consumerfeign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import com.gmailsystem.dto.EmailDto;
import java.util.List;
import java.util.Map;
@FeignClient(name = "graphql-server", url = "http://localhost:8086")
public interface GmailApiClient {

    @PostMapping("/graphql")
    String executeGraphql(@RequestBody Map<String, Object> body);
}
