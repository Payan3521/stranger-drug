package com.microservicethree.videos.domain.port.in;

import org.springframework.data.domain.Page;

import com.microservicethree.videos.infraestructure.dto.VideoResponse;

public interface ISearchVideos {

     public Page<VideoResponse> execute(VideoSearchRequest request)
}
