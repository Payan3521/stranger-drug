package com.microservicethree.videos.domain.port;

import com.microservicethree.videos.domain.model.Video;

public interface VideoProcessingService {

    void processVideoAsync(Video video);

    String generateThumbnai(String videoUrl, String outputPath);

    String generatePreview(String videoUrl, String outputPath, int durationSeconds);

    int getVideoDuration(String videoUrl);
}
