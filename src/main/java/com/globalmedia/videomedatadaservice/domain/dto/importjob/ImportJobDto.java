package com.globalmedia.videomedatadaservice.domain.dto.importjob;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.globalmedia.videomedatadaservice.domain.model.ImportJob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportJobDto {
    
    private Long id;
    private String source;
    private Integer requestedCount;
    private Integer importedCount;
    private ImportJob.ImportStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;
    
    private String errorMessage;
}