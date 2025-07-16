package com.globalmedia.videometadataservice.service;

import com.globalmedia.videometadataservice.domain.model.ImportJob;
import com.globalmedia.videometadataservice.repository.ImportJobRepository;
import com.globalmedia.videometadataservice.domain.dto.ImportJobDto;
import com.globalmedia.videometadataservice.domain.mapper.ImportJobMapper;
import com.globalmedia.videometadataservice.exception.ImportJobNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportJobServiceTest {

    @Mock
    private ImportJobRepository importJobRepository;

    @Mock
    private ImportJobMapper importJobMapper;

    @InjectMocks
    private ImportJobService importJobService;

    private ImportJob testImportJob;
    private ImportJobDto testImportJobDto;

    @BeforeEach
    void setUp() {
        testImportJob = ImportJob.builder()
                .id(1L)
                .source("YouTube")
                .requestedCount(5)
                .importedCount(3)
                .status(ImportJob.ImportStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .build();

        testImportJobDto = ImportJobDto.builder()
                .id(1L)
                .source("YouTube")
                .requestedCount(5)
                .importedCount(3)
                .status(ImportJob.ImportStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createImportJob_ReturnsCreatedJob() {
        ImportJob newJob = ImportJob.builder()
                .source("YouTube")
                .requestedCount(5)
                .status(ImportJob.ImportStatus.PENDING)
                .build();

        when(importJobRepository.save(any(ImportJob.class))).thenReturn(newJob);
        when(importJobMapper.toDto(newJob)).thenReturn(testImportJobDto);

        ImportJobDto result = importJobService.createImportJob("YouTube", 5);

        assertNotNull(result);
        assertEquals(testImportJobDto.getId(), result.getId());
        assertEquals(testImportJobDto.getSource(), result.getSource());

        verify(importJobRepository).save(any(ImportJob.class));
        verify(importJobMapper).toDto(newJob);
    }

    @Test
    void getImportJob_ExistingId_ReturnsJob() {
        when(importJobRepository.findById(1L)).thenReturn(Optional.of(testImportJob));
        when(importJobMapper.toDto(testImportJob)).thenReturn(testImportJobDto);

        ImportJobDto result = importJobService.getImportJob(1L);

        assertNotNull(result);
        assertEquals(testImportJobDto.getId(), result.getId());
        assertEquals(testImportJobDto.getSource(), result.getSource());

        verify(importJobRepository).findById(1L);
        verify(importJobMapper).toDto(testImportJob);
    }

    @Test
    void getImportJob_NonExistingId_ThrowsException() {
        when(importJobRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ImportJobNotFoundException.class, () -> {
            importJobService.getImportJob(999L);
        });

        verify(importJobRepository).findById(999L);
        verifyNoInteractions(importJobMapper);
    }

    @Test
    void getAllImportJobs_ReturnsAllJobs() {
        List<ImportJob> jobs = Arrays.asList(testImportJob);
        List<ImportJobDto> jobDtos = Arrays.asList(testImportJobDto);

        when(importJobRepository.findAll()).thenReturn(jobs);
        when(importJobMapper.toDtoList(jobs)).thenReturn(jobDtos);

        List<ImportJobDto> result = importJobService.getAllImportJobs();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testImportJobDto.getId(), result.get(0).getId());

        verify(importJobRepository).findAll();
        verify(importJobMapper).toDtoList(jobs);
    }

    @Test
    void getAllImportJobs_WithPagination_ReturnsPagedJobs() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ImportJob> jobs = Arrays.asList(testImportJob);
        Page<ImportJob> jobPage = new PageImpl<>(jobs, pageable, 1);

        when(importJobRepository.findAll(pageable)).thenReturn(jobPage);
        when(importJobMapper.toDto(testImportJob)).thenReturn(testImportJobDto);

        Page<ImportJobDto> result = importJobService.getAllImportJobs(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testImportJobDto.getId(), result.getContent().get(0).getId());

        verify(importJobRepository).findAll(pageable);
        verify(importJobMapper).toDto(testImportJob);
    }

    @Test
    void getImportJobsByStatus_ReturnsFilteredJobs() {
        List<ImportJob> jobs = Arrays.asList(testImportJob);
        List<ImportJobDto> jobDtos = Arrays.asList(testImportJobDto);

        when(importJobRepository.findByStatus(ImportJob.ImportStatus.COMPLETED)).thenReturn(jobs);
        when(importJobMapper.toDtoList(jobs)).thenReturn(jobDtos);

        List<ImportJobDto> result = importJobService.getImportJobsByStatus(ImportJob.ImportStatus.COMPLETED);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testImportJobDto.getStatus(), result.get(0).getStatus());

        verify(importJobRepository).findByStatus(ImportJob.ImportStatus.COMPLETED);
        verify(importJobMapper).toDtoList(jobs);
    }

    @Test
    void getImportJobsByStatus_WithPagination_ReturnsPagedFilteredJobs() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ImportJob> jobs = Arrays.asList(testImportJob);
        Page<ImportJob> jobPage = new PageImpl<>(jobs, pageable, 1);

        when(importJobRepository.findByStatus(ImportJob.ImportStatus.COMPLETED, pageable)).thenReturn(jobPage);
        when(importJobMapper.toDto(testImportJob)).thenReturn(testImportJobDto);

        Page<ImportJobDto> result = importJobService.getImportJobsByStatus(ImportJob.ImportStatus.COMPLETED, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testImportJobDto.getStatus(), result.getContent().get(0).getStatus());

        verify(importJobRepository).findByStatus(ImportJob.ImportStatus.COMPLETED, pageable);
        verify(importJobMapper).toDto(testImportJob);
    }

    @Test
    void processImportJob_CompletesSuccessfully() {
        when(importJobRepository.findById(1L)).thenReturn(Optional.of(testImportJob));
        when(importJobRepository.save(any(ImportJob.class))).thenReturn(testImportJob);
        when(importJobMapper.toDto(testImportJob)).thenReturn(testImportJobDto);

        CompletableFuture<ImportJobDto> result = importJobService.processImportJob(1L);

        assertNotNull(result);
        verify(importJobRepository).findById(1L);
        verify(importJobRepository, atLeastOnce()).save(any(ImportJob.class));
    }

    @Test
    void getImportJobsBySource_ReturnsFilteredJobs() {
        List<ImportJob> jobs = Arrays.asList(testImportJob);
        List<ImportJobDto> jobDtos = Arrays.asList(testImportJobDto);

        when(importJobRepository.findBySource("youtube")).thenReturn(jobs);
        when(importJobMapper.toDtoList(jobs)).thenReturn(jobDtos);

        List<ImportJobDto> result = importJobService.getImportJobsBySource("youtube");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testImportJobDto.getSource(), result.get(0).getSource());

        verify(importJobRepository).findBySource("youtube");
        verify(importJobMapper).toDtoList(jobs);
    }

    @Test
    void getImportJobsBySource_WithPagination_ReturnsPagedFilteredJobs() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ImportJob> jobs = Arrays.asList(testImportJob);
        Page<ImportJob> jobPage = new PageImpl<>(jobs, pageable, 1);

        when(importJobRepository.findBySource("youtube", pageable)).thenReturn(jobPage);
        when(importJobMapper.toDto(testImportJob)).thenReturn(testImportJobDto);

        Page<ImportJobDto> result = importJobService.getImportJobsBySource("youtube", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testImportJobDto.getSource(), result.getContent().get(0).getSource());

        verify(importJobRepository).findBySource("youtube", pageable);
        verify(importJobMapper).toDto(testImportJob);
    }
}
