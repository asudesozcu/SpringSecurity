package dto;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)

public class EmailDto {
    private String emailId;
    private String subject;
    private String sender;
    private String snippet;
    private LocalDateTime receivedAt;
    private List<String> labels;
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
                ", labels=" + labels +
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

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
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
        this.labels = labels;
        this.hasAttachment = hasAttachment;
        this.sizeEstimate = sizeEstimate;
    }
//kafka için gerekli belki
    public EmailDto() {}

}
