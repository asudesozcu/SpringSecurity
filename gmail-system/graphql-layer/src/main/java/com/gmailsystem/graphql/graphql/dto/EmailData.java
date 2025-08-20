package com.gmailsystem.graphql.graphql.dto;

import com.gmailsystem.dto.EmailDto;
import lombok.Data;

import java.util.List;

@Data
public class EmailData {
    private List<EmailDto> getEmails;
}