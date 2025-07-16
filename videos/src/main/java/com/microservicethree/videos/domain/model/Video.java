package com.microservicethree.videos.domain.model;

import java.time.LocalDateTime;

public class Video {
    private Long id;
    private String title;
    private String description;
    private String originalFilename;
    private Long fileSize;
    private Integer durationSeconds;
    private String videoUrl;
    private String thumbnailUrl;
    private String previewUrl;
    private VideoStatus status;
    private String contentType;
    private String uploadedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Video() {}

    public Video(Long id, String title, String description, String originalFilename, Long fileSize, String videoUrl,
                String contentType, String uploadedBy){
        this.id = id;
        this.title = title;
        this.description = description;
        this.originalFilename = originalFilename;
        this.fileSize = fileSize;
        this.videoUrl = videoUrl;
        this.contentType = contentType;
        this.uploadedBy = uploadedBy;
        this.status = VideoStatus.PROCESSING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsReady(){
        this.status = VideoStatus.READY;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFailed(){
        this.status = VideoStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDeleted(){
        this.status = VideoStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateMetaData(String title, String description){
        this.title = title;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setProcessingResults(Integer durationSeconds, String thumbnaiUrl, String previewUrl){
        this.durationSeconds = durationSeconds;
        this.thumbnailUrl = thumbnaiUrl;
        this.previewUrl = previewUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isReady(){
        return VideoStatus.READY.equals(this.status);
    }

    public boolean isDeleted(){
        return VideoStatus.DELETED.equals(this.status);
    }

    public Long getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String getOriginalFilename(){
        return originalFilename;
    }

    public Long getFileSize(){
        return fileSize;
    }

    public Integer getDurationSeconds() { 
        return durationSeconds; 
    }

    public String getVideoUrl() {
        return videoUrl; 
    }

    public String getThumbnailUrl() { 
        return thumbnailUrl; 
    }

    public String getPreviewUrl() { 
        return previewUrl; 
    }
    
    public VideoStatus getStatus() { 
        return status; 
    }

    public String getContentType() { 
        return contentType; 
    }

    public String getUploadedBy() { 
        return uploadedBy; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }

    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }

    public void setUpdatedAt(LocalDateTime updatedAt) { 
        this.updatedAt = updatedAt; 
    }

    public void setStatus(VideoStatus status) { 
        this.status = status; 
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds; 
    }

    public void setThumbnailUrl(String thumbnailUrl) { 
        this.thumbnailUrl = thumbnailUrl; 
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
}
