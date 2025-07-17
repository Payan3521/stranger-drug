package com.microservicethree.videos.infraestructure.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class VideoSearchRequest {

    @Size(max = 100, message = "Search term must not exceed 100 characters")
    private String searchTerm;

    @Min(value = 0, message = "Page must be non-negative")
    private int page = 0;

    @Min(value = 1, message = "Size must be positive")
    private int size = 10;

    private String sortBy = "createdAt";
    private String sortDirection = "desc";

    public VideoSearchRequest() {}

    public VideoSearchRequest(String searchTerm, int page, int size) {
        this.searchTerm = searchTerm;
        this.page = page;
        this.size = size;
    }
}
