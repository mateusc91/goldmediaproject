package com.globalmedia.videometadataservice.domain.mapper;

import com.globalmedia.videometadataservice.domain.model.Video;
import com.globalmedia.videometadataservice.domain.request.VideoCreateRequest;
import com.globalmedia.videometadataservice.domain.dto.VideoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VideoMapper {

    VideoDto toDto(Video video);

    List<VideoDto> toDtoList(List<Video> videos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Video toEntity(VideoCreateRequest request);
}