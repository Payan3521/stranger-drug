package com.microservicethree.videos.infraestructure.dto;

import java.time.LocalDateTime;
import com.microservicethree.videos.domain.model.VideoStatus;

public class VideoResponse {

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

    public VideoResponse() {}

    public Long getId(){
        return id; 
    }

    public void setId(Long id){
        this.id = id; 
    }
    
    public String getTitle(){ 
        return title; 
    }

    public void setTitle(String title){ 
        this.title = title;
    }
    
    public String getDescription(){ 
        return description; 
    }

    public void setDescription(String description){ 
        this.description = description; 
    }
    
    public String getOriginalFilename(){ 
        return originalFilename; 
    }

    public void setOriginalFilename(String originalFilename){ 
        this.originalFilename = originalFilename; 
    }
    
    public Long getFileSize(){ 
        return fileSize;
    }
    
    public void setFileSize(Long fileSize){ 
        this.fileSize = fileSize; 
    }
    
    public Integer getDurationSeconds(){ 
        return durationSeconds; 
    }

    public void setDurationSeconds(Integer durationSeconds){ 
        this.durationSeconds = durationSeconds; 
    }
    
    public String getVideoUrl(){ 
        return videoUrl; 
    }

    public void setVideoUrl(String videoUrl){ 
        this.videoUrl = videoUrl; 
    }
    
    public String getThumbnailUrl(){ 
        return thumbnailUrl; 
    }

    public void setThumbnailUrl(String thumbnailUrl){ 
        this.thumbnailUrl = thumbnailUrl; 
    }
    
    public String getPreviewUrl(){ 
        return previewUrl; 
    }

    public void setPreviewUrl(String previewUrl){ 
        this.previewUrl = previewUrl; 
    }
    
    public VideoStatus getStatus(){ 
        return status; 
    }

    public void setStatus(VideoStatus status){ 
        this.status = status; 
    }
    
    public String getContentType(){ 
        return contentType; 
    }

    public void setContentType(String contentType){ 
        this.contentType = contentType; 
    }
    
    public String getUploadedBy(){ 
        return uploadedBy; 
    }

    public void setUploadedBy(String uploadedBy){ 
        this.uploadedBy = uploadedBy; 
    }
    
    public LocalDateTime getCreatedAt(){ 
        return createdAt; 
    }

    public void setCreatedAt(LocalDateTime createdAt){ 
        this.createdAt = createdAt; 
    }
    
    public LocalDateTime getUpdatedAt(){ 
        return updatedAt; 
    }

    public void setUpdatedAt(LocalDateTime updatedAt){ 
        this.updatedAt = updatedAt; 
    }
}

