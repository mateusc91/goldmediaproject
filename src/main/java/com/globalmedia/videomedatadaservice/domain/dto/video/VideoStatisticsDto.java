package com.globalmedia.videomedatadaservice.domain.dto.video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoStatisticsDto {
    
    private Map<String, Long> videoCountBySource;
    private Map<String, String> averageDurationBySource;

    private long totalVideos;
}