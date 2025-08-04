package com.gmailsystem.graphql.graphql.dto;

import com.gmailsystem.dto.EmailDto;
import lombok.Data;

import java.util.List;

@Data
public class GraphqlFeignResponse<T> {
    private T data;
}




