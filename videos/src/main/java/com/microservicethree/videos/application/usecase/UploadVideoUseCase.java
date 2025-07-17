package com.microservicethree.videos.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.microservicethree.videos.domain.exception.VideoProcessingException;
import com.microservicethree.videos.domain.model.Video;
import com.microservicethree.videos.domain.port.in.IUploadVideo;
import com.microservicethree.videos.domain.port.out.StorageService;
import com.microservicethree.videos.domain.port.out.VideoProcessingService;
import com.microservicethree.videos.domain.port.out.VideoRepository;
import com.microservicethree.videos.infraestructure.dto.VideoResponse;
import com.microservicethree.videos.infraestructure.dto.VideoUploadRequest;
import com.microservicethree.videos.infraestructure.mapper.VideoMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UploadVideoUseCase implements IUploadVideo{

    private final VideoRepository VideoRepository;
    private final VideoMapper videoMapper;
    private final StorageService storageService;
    private final VideoProcessingService videoProcessingService;

    @Override
    public VideoResponse execute(VideoUploadRequest request, MultipartFile file) {
        
        try{
            validateVideoFile(file);

            String videoUrl = storageService.uploadFile(file, "videos");

            Video video = new Video(
                null,
                request.getTitle(),
                request.getDescription(),
                file.getOriginalFilename(),
                file.getSize(),
                videoUrl,
                file.getContentType(),
                request.getUploadBy()
            );

            video = VideoRepository.save(video);
            videoProcessingService.processVideoAsync(video);

            return videoMapper.toResponse(video);
        } catch(Exception e){
            throw new VideoProcessingException("Failed to upload video: " + e.getMessage());
        }
    }

    private void validateVideoFile(MultipartFile file){

        if (file.isEmpty()) {
            throw new VideoProcessingException("File is empty");
        }

        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith("video/")){
            throw new VideoProcessingException("File must be a video");
        }

        long maxSize = 500 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new VideoProcessingException("File size exceeds maxium allowed size");
        }
    }
}
