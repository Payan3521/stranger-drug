package com.microservicethree.videos.domain.port.in;

import com.microservicethree.videos.infraestructure.dto.VideoResponse;
import com.microservicethree.videos.infraestructure.dto.VideoUploadRequest;

public interface IUpdateMetaData {

    public VideoResponse execute(Long id, VideoUploadRequest request);
    
}
