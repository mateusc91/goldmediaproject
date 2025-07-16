package com.globalmedia.videomedatadaservice.domain.dto.video;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoCreateRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotBlank(message = "URL is required")
    private String url;
    
    @NotBlank(message = "Source is required")
    private String source;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Upload date must be in the past or present")
    private LocalDate uploadDate;
    
    private Integer durationInSeconds;
}