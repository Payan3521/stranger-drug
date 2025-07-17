package com.microservicethree.videos.infraestructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VideoUploadRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotBlank(message = "Uploaded by is required")
    private String uploadBy;

    public VideoUploadRequest(String title, String description, String uploadBy){
        this.title = title;
        this.description = description;
        this.uploadBy = uploadBy;
    }

    public VideoUploadRequest(){

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

    public String getUploadBy(){
        return uploadBy;
    }

    public void setUploadBy(String uploadBy){
        this.uploadBy = uploadBy;
    }
}
