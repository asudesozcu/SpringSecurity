package com.gmailsystem.consumerkafka.repo;

import com.gmailsystem.dto.EmailDto;
import com.gmailsystem.consumerkafka.EmailEntity;

public class EmailMapper {

        public static EmailEntity fromDtotoEntity(EmailDto dto) {
            EmailEntity entity = new EmailEntity();
            entity.setEmailId(dto.getEmailId());
            entity.setSubject(dto.getSubject());
            entity.setSender(dto.getSender());
            entity.setSnippet(dto.getSnippet());
            entity.setReceivedAt(dto.getReceivedAt());
            entity.setLabelIds(dto.getLabelIds());
            entity.setHasAttachment(dto.isHasAttachment());
            entity.setSizeEstimate(dto.getSizeEstimate());
            return entity;
        }

        public static EmailDto fromEntitytoDto(EmailEntity entity) {
            EmailDto dto = new EmailDto();
            dto.setEmailId(entity.getEmailId());
            dto.setSubject(entity.getSubject());
            dto.setSender(entity.getSender());
            dto.setSnippet(entity.getSnippet());
            dto.setReceivedAt(entity.getReceivedAt());
            dto.setLabelIds(entity.getLabelIds());
            dto.setHasAttachment(entity.isHasAttachment());
            dto.setSizeEstimate(entity.getSizeEstimate());
            return dto;
        }
    }


