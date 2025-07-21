package dto;
import java.time.LocalDateTime;
import java.util.List;
import common.EmailDtoProto ;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailDto {
    private String emailId;
    private String subject;
    private String sender;
    private String snippet;
    private LocalDateTime receivedAt;
    private List<String> labelIds;
    private boolean hasAttachment;
    private int sizeEstimate;

    public String getEmailId() {
        return emailId;
    }

    @Override
    public String toString() {
        return "EmailDto{" +
                "emailId='" + emailId + '\'' +
                ", subject='" + subject + '\'' +
                ", sender='" + sender + '\'' +
                ", snippet='" + snippet + '\'' +
                ", receivedAt=" + receivedAt +
                ", labelIds=" + labelIds +
                ", hasAttachment=" + hasAttachment +
                ", sizeEstimate=" + sizeEstimate +
                '}';
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public List<String> getLabelIds() {
        return labelIds;
    }

    public void setLabelIds(List<String> labelIds) {
        this.labelIds = labelIds;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public int getSizeEstimate() {
        return sizeEstimate;
    }

    public void setSizeEstimate(int sizeEstimate) {
        this.sizeEstimate = sizeEstimate;
    }

    public EmailDto(String emailId, String subject, String sender, String snippet, LocalDateTime receivedAt, List<String> labels, boolean hasAttachment, int sizeEstimate) {
        this.emailId = emailId;
        this.subject = subject;
        this.sender = sender;
        this.snippet = snippet;
        this.receivedAt = receivedAt;
        this.labelIds = labels;
        this.hasAttachment = hasAttachment;
        this.sizeEstimate = sizeEstimate;
    }
//kafka için gerekli belki
    public EmailDto() {}

    public common.EmailDtoProto toProto() {
        return common.EmailDtoProto.newBuilder()
                .setEmailId(emailId != null ? emailId : "")
                .setSubject(subject != null ? subject : "")
                .setSender(sender != null ? sender : "")
                .setSnippet(snippet != null ? snippet : "")
                .setReceivedAt(receivedAt != null ? receivedAt.toString() : "")
                .addAllLabels(labelIds != null ? labelIds : List.of())
                .setHasAttachment(hasAttachment)
                .setSizeEstimate(sizeEstimate)
                .build();
    }
    public static EmailDto fromProto(EmailDtoProto proto) {
        EmailDto dto = new EmailDto();

        dto.setEmailId(proto.getEmailId());
        dto.setSubject(proto.getSubject());
        dto.setSender(proto.getSender());
        dto.setSnippet(proto.getSnippet());

        try {
            dto.setReceivedAt(LocalDateTime.parse(proto.getReceivedAt()));
        } catch (Exception e) {
            dto.setReceivedAt(null); // veya LocalDateTime.now()
        }

        dto.setLabelIds(proto.getLabelsList());
        dto.setHasAttachment(proto.getHasAttachment());
        dto.setSizeEstimate(proto.getSizeEstimate());

        return dto;
    }
}
