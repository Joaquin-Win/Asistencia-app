package com.asistencia.model;

import java.time.LocalDateTime;

public class TaskUpload {

    private Long id;
    private Long meetingId;
    private String uploaderUsername;
    private String uploaderDisplayName;
    private String title;
    private String description;
    private String fileContent;
    private LocalDateTime uploadedAt;

    public TaskUpload() {}

    public TaskUpload(Long id, Long meetingId, String uploaderUsername,
                      String uploaderDisplayName, String title, String description,
                      String fileContent, LocalDateTime uploadedAt) {
        this.id = id;
        this.meetingId = meetingId;
        this.uploaderUsername = uploaderUsername;
        this.uploaderDisplayName = uploaderDisplayName;
        this.title = title;
        this.description = description;
        this.fileContent = fileContent;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }

    public String getUploaderUsername() { return uploaderUsername; }
    public void setUploaderUsername(String uploaderUsername) { this.uploaderUsername = uploaderUsername; }

    public String getUploaderDisplayName() { return uploaderDisplayName; }
    public void setUploaderDisplayName(String uploaderDisplayName) { this.uploaderDisplayName = uploaderDisplayName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFileContent() { return fileContent; }
    public void setFileContent(String fileContent) { this.fileContent = fileContent; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
