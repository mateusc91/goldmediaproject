package com.globalmedia.videometadataservice.controller;

import com.globalmedia.videometadataservice.client.ExternalVideoMockClient;
import com.globalmedia.videometadataservice.service.VideoService;
import com.globalmedia.videometadataservice.domain.model.Pagination;
import com.globalmedia.videometadataservice.domain.request.VideoCreateRequest;
import com.globalmedia.videometadataservice.domain.dto.VideoDto;
import com.globalmedia.videometadataservice.domain.dto.VideoStatisticsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
@Tag(name = "Videos", description = "Video Metadata API")
@SecurityRequirement(name = "bearerAuth")
public class VideoController {

    private final VideoService videoService;
    private final ExternalVideoMockClient externalVideoMockClient;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get all videos", description = "Returns paginated videos with optional filters and sorting")
    public ResponseEntity<Pagination<VideoDto>> getAllVideos(
            @Parameter(description = "Filter by source (e.g., YouTube, Vimeo)")
            @RequestParam(required = false) String source,

            @Parameter(description = "Filter by upload date (start)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Filter by upload date (end)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(description = "Filter by minimum duration in seconds")
            @RequestParam(required = false) Integer minDuration,

            @Parameter(description = "Filter by maximum duration in seconds")
            @RequestParam(required = false) Integer maxDuration,

            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sort field (e.g., title, uploadDate, durationInSeconds)")
            @RequestParam(defaultValue = "id") String sort,

            @Parameter(description = "Sort direction (ASC or DESC)")
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(videoService.getPaginatedVideos(source, startDate, endDate, minDuration, maxDuration, page, size, sort, direction));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get video by ID", description = "Returns a single video by its ID")
    public ResponseEntity<VideoDto> getVideoById(
            @Parameter(description = "Video ID") @PathVariable Long id
    ) {
        return ResponseEntity.ok(videoService.getVideoById(id));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a video", description = "Creates a new video (admin only)")
    public ResponseEntity<VideoDto> createVideo(
            @Valid @RequestBody VideoCreateRequest request
    ) {
        return ResponseEntity.ok(videoService.createVideo(request));
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Import videos", description = "Imports videos from external sources (admin only)")
    public ResponseEntity<List<VideoDto>> importVideos(
            @Parameter(description = "Source to import from (leave empty for all sources)")
            @RequestParam(required = false) String source,

            @Parameter(description = "Number of videos to import per source")
            @RequestParam(defaultValue = "5") int count
    ) {
        List<VideoCreateRequest> videosToImport = externalVideoMockClient.fetchVideos(source, count);
        return ResponseEntity.ok(videoService.importVideos(videosToImport));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get video statistics", description = "Returns statistics about videos")
    public ResponseEntity<VideoStatisticsDto> getVideoStatistics() {
        return ResponseEntity.ok(videoService.getVideoStatistics());
    }
}
