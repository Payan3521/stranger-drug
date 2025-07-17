package com.microservicethree.videos.domain.port.out;

import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import com.microservicethree.videos.domain.model.Video;
import com.microservicethree.videos.domain.model.VideoStatus;

public interface VideoRepository {

    Video save(Video video);

    Optional<Video> findById(Long id);

    Optional<Video> findByIdAndStatus(Long id, VideoStatus status);

    Page<Video> finddByStatusOrderByCreatedAtDesc(VideoStatus status, Pageable pageable);

    Page<Video> searchByStatusAndTerm(VideoStatus status, String searchTerm, Pageable pageable);

    List<Video> findByUploadedByAndStatus(String uploadedBy, VideoStatus status);

    void deleteById(Long id);

    long countByStatus(VideoStatus status);

    long getTotalFileSizeByStatus();

}
