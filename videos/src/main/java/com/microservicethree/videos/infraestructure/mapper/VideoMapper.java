package com.microservicethree.videos.infraestructure.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.microservicethree.videos.domain.model.Video;
import com.microservicethree.videos.infraestructure.dto.VideoResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VideoMapper {

    VideoResponse toResponse(Video video);

    List<Video> toResponse(List<Video> videos);
}
