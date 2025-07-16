package com.globalmedia.videomedatadaservice.domain.mapper;

import com.globalmedia.videomedatadaservice.domain.model.Video;
import com.globalmedia.videomedatadaservice.domain.dto.video.VideoCreateRequest;
import com.globalmedia.videomedatadaservice.domain.dto.video.VideoDto;
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