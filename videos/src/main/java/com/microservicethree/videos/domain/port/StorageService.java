package com.microservicethree.videos.domain.port;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String uploadFile(MultipartFile file, String folder);

    void deleteFIle(String fileUrl);

    String generatePresigneUrl(String fileKey, int expirationMinutes);
}
