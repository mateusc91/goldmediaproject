package com.globalmedia.videometadataservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDto {
    
    private Long id;
    private String title;
    private String description;
    private String url;
    private String source;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate uploadDate;
    
    private Integer durationInSeconds;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}