package com.globalmedia.videometadataservice.controller;

import com.globalmedia.videometadataservice.domain.model.ImportJob;
import com.globalmedia.videometadataservice.service.ImportJobService;
import com.globalmedia.videometadataservice.domain.model.Pagination;
import com.globalmedia.videometadataservice.domain.dto.ImportJobDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/import-jobs")
@RequiredArgsConstructor
@Tag(name = "Import Jobs", description = "Import Jobs API")
@SecurityRequirement(name = "bearerAuth")
public class ImportJobController {

    private final ImportJobService importJobService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create an import job", description = "Creates a new asynchronous import job (admin only)")
    public ResponseEntity<ImportJobDto> createImportJob(
            @Parameter(description = "Source to import from (leave empty for all sources)")
            @RequestParam(required = false) String source,

            @Parameter(description = "Number of videos to import per source")
            @RequestParam(defaultValue = "5") int count
    ) {
        ImportJobDto job = importJobService.createImportJob(source, count);

        importJobService.processImportJob(job.getId());

        return ResponseEntity.accepted().body(job);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get import job by ID", description = "Returns a single import job by its ID (admin only)")
    public ResponseEntity<ImportJobDto> getImportJob(
            @Parameter(description = "Import Job ID") @PathVariable Long id
    ) {
        return ResponseEntity.ok(importJobService.getImportJob(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all import jobs", description = "Returns paginated import jobs with optional filters and sorting (admin only)")
    public ResponseEntity<Pagination<ImportJobDto>> getAllImportJobs(
            @Parameter(description = "Filter by status (PENDING, IN_PROGRESS, COMPLETED, FAILED)")
            @RequestParam(required = false) ImportJob.ImportStatus status,

            @Parameter(description = "Filter by source")
            @RequestParam(required = false) String source,

            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sort field (e.g., createdAt, status)")
            @RequestParam(defaultValue = "createdAt") String sort,

            @Parameter(description = "Sort direction (ASC or DESC)")
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        return ResponseEntity.ok(importJobService.getPaginatedImportJobs(status, source, page, size, sort, direction));
    }
}
