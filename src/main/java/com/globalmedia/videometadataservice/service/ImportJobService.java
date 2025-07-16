package com.globalmedia.videometadataservice.service;

import com.globalmedia.videometadataservice.client.ExternalVideoMockClient;
import com.globalmedia.videometadataservice.domain.model.ImportJob;
import com.globalmedia.videometadataservice.repository.ImportJobRepository;
import com.globalmedia.videometadataservice.domain.model.Pagination;
import com.globalmedia.videometadataservice.domain.dto.ImportJobDto;
import com.globalmedia.videometadataservice.domain.dto.VideoDto;
import com.globalmedia.videometadataservice.domain.mapper.ImportJobMapper;
import com.globalmedia.videometadataservice.exception.ImportJobNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportJobService {

    private final ImportJobRepository importJobRepository;
    private final ExternalVideoMockClient externalVideoMockClient;
    private final VideoService videoService;
    private final ImportJobMapper importJobMapper;

    @Transactional
    public ImportJobDto createImportJob(String source, int count) {
        ImportJob job = ImportJob.builder()
                .source(source != null ? source : "ALL")
                .requestedCount(count)
                .status(ImportJob.ImportStatus.PENDING)
                .build();

        ImportJob savedJob = importJobRepository.save(job);
        return importJobMapper.toDto(savedJob);
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<ImportJobDto> processImportJob(Long jobId) {
        ImportJob job = importJobRepository.findById(jobId)
                .orElseThrow(() -> new ImportJobNotFoundException(jobId));

        try {
            job.setStatus(ImportJob.ImportStatus.IN_PROGRESS);
            importJobRepository.save(job);

            List<VideoDto> importedVideos;
            if ("ALL".equals(job.getSource())) {
                importedVideos = videoService.importVideos(
                        externalVideoMockClient.fetchVideosFromAllSources(job.getRequestedCount())
                );
            } else {
                importedVideos = videoService.importVideos(
                        externalVideoMockClient.fetchVideosFromExternalSource(job.getSource(), job.getRequestedCount())
                );
            }

            job.setImportedCount(importedVideos.size());
            job.setStatus(ImportJob.ImportStatus.COMPLETED);
            job.setCompletedAt(LocalDateTime.now());

            log.info("Import job {} completed successfully. Imported {} videos.", jobId, importedVideos.size());
        } catch (Exception e) {
            job.setStatus(ImportJob.ImportStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            job.setCompletedAt(LocalDateTime.now());

            log.error("Import job {} failed: {}", jobId, e.getMessage(), e);
        }

        ImportJob savedJob = importJobRepository.save(job);
        return CompletableFuture.completedFuture(importJobMapper.toDto(savedJob));
    }

    @Transactional(readOnly = true)
    public ImportJobDto getImportJob(Long id) {
        ImportJob job = importJobRepository.findById(id)
                .orElseThrow(() -> new ImportJobNotFoundException(id));
        return importJobMapper.toDto(job);
    }

    @Transactional(readOnly = true)
    public List<ImportJobDto> getAllImportJobs() {
        List<ImportJob> jobs = importJobRepository.findAll();
        return importJobMapper.toDtoList(jobs);
    }

    @Transactional(readOnly = true)
    public Page<ImportJobDto> getAllImportJobs(Pageable pageable) {
        Page<ImportJob> jobPage = importJobRepository.findAll(pageable);
        return jobPage.map(importJobMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ImportJobDto> getImportJobsByStatus(ImportJob.ImportStatus status) {
        List<ImportJob> jobs = importJobRepository.findByStatus(status);
        return importJobMapper.toDtoList(jobs);
    }

    @Transactional(readOnly = true)
    public Page<ImportJobDto> getImportJobsByStatus(ImportJob.ImportStatus status, Pageable pageable) {
        Page<ImportJob> jobPage = importJobRepository.findByStatus(status, pageable);
        return jobPage.map(importJobMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ImportJobDto> getImportJobsBySource(String source) {
        List<ImportJob> jobs = importJobRepository.findBySource(source);
        return importJobMapper.toDtoList(jobs);
    }

    @Transactional(readOnly = true)
    public Page<ImportJobDto> getImportJobsBySource(String source, Pageable pageable) {
        Page<ImportJob> jobPage = importJobRepository.findBySource(source, pageable);
        return jobPage.map(importJobMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Pagination<ImportJobDto> getPaginatedImportJobs(ImportJob.ImportStatus status, String source, int page, int size, String sort, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<ImportJobDto> resultPage;

        if (status != null) {
            resultPage = getImportJobsByStatus(status, pageable);
        } else if (source != null) {
            resultPage = getImportJobsBySource(source, pageable);
        } else {
            resultPage = getAllImportJobs(pageable);
        }

        return Pagination.from(resultPage, sort, direction);
    }
}
