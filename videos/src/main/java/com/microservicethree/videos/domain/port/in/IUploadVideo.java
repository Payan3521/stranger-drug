package com.microservicethree.videos.domain.port.in;

import org.springframework.web.multipart.MultipartFile;

import com.microservicethree.videos.infraestructure.dto.VideoResponse;
import com.microservicethree.videos.infraestructure.dto.VideoUploadRequest;

public interface IUploadVideo {

    public VideoResponse execute(VideoUploadRequest request, MultipartFile file);
    
}
