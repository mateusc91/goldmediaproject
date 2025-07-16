package com.globalmedia.videometadataservice.web.controller;

import com.globalmedia.videometadataservice.controller.ImportJobController;
import com.globalmedia.videometadataservice.domain.model.ImportJob;
import com.globalmedia.videometadataservice.service.ImportJobService;
import com.globalmedia.videometadataservice.domain.model.Pagination;
import com.globalmedia.videometadataservice.domain.dto.ImportJobDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ImportJobControllerTest {

    @Mock
    private ImportJobService importJobService;

    @InjectMocks
    private ImportJobController importJobController;

    private MockMvc mockMvc;
    private ImportJobDto testImportJobDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(importJobController).build();

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
    void createImportJob_ReturnsCreatedJob() throws Exception {
        when(importJobService.createImportJob(eq("YouTube"), eq(5))).thenReturn(testImportJobDto);
        when(importJobService.processImportJob(anyLong())).thenReturn(CompletableFuture.completedFuture(testImportJobDto));

        mockMvc.perform(post("/import-jobs?source=YouTube&count=5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.source", is("YouTube")))
                .andExpect(jsonPath("$.requestedCount", is(5)));

        verify(importJobService).createImportJob(eq("YouTube"), eq(5));
        verify(importJobService).processImportJob(anyLong());
    }

    @Test
    void getImportJob_ExistingId_ReturnsJob() throws Exception {
        when(importJobService.getImportJob(1L)).thenReturn(testImportJobDto);

        mockMvc.perform(get("/import-jobs/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.source", is("YouTube")))
                .andExpect(jsonPath("$.status", is("COMPLETED")));

        verify(importJobService).getImportJob(1L);
    }

    @Test
    void getAllImportJobs_ReturnsPagedResponse() throws Exception {
        List<ImportJobDto> jobs = Arrays.asList(testImportJobDto);
        Page<ImportJobDto> page = new PageImpl<>(jobs, PageRequest.of(0, 10), 1);
        when(importJobService.getPaginatedImportJobs(eq(null), eq(null), eq(0), eq(10), eq("createdAt"), eq("DESC"))).thenReturn(Pagination.from(page, "createdAt", "DESC"));

        mockMvc.perform(get("/import-jobs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].source", is("YouTube")))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.sort", is("createdAt")))
                .andExpect(jsonPath("$.direction", is("DESC")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(importJobService).getPaginatedImportJobs(eq(null), eq(null), eq(0), eq(10), eq("createdAt"), eq("DESC"));
    }

    @Test
    void getAllImportJobs_WithStatusFilter_ReturnsFilteredPagedResponse() throws Exception {
        List<ImportJobDto> jobs = Arrays.asList(testImportJobDto);
        Page<ImportJobDto> page = new PageImpl<>(jobs, PageRequest.of(0, 10), 1);
        when(importJobService.getPaginatedImportJobs(eq(ImportJob.ImportStatus.COMPLETED), eq(null), eq(0), eq(10), eq("createdAt"), eq("DESC"))).thenReturn(Pagination.from(page, "createdAt", "DESC"));

        mockMvc.perform(get("/import-jobs?status=COMPLETED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].status", is("COMPLETED")))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.sort", is("createdAt")))
                .andExpect(jsonPath("$.direction", is("DESC")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(importJobService).getPaginatedImportJobs(eq(ImportJob.ImportStatus.COMPLETED), eq(null), eq(0), eq(10), eq("createdAt"), eq("DESC"));
    }

    @Test
    void getAllImportJobs_WithSourceFilter_ReturnsFilteredPagedResponse() throws Exception {
        List<ImportJobDto> jobs = Arrays.asList(testImportJobDto);
        Page<ImportJobDto> page = new PageImpl<>(jobs, PageRequest.of(0, 10), 1);
        when(importJobService.getPaginatedImportJobs(eq(null), eq("YouTube"), eq(0), eq(10), eq("createdAt"), eq("DESC"))).thenReturn(Pagination.from(page, "createdAt", "DESC"));

        mockMvc.perform(get("/import-jobs?source=YouTube")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].source", is("YouTube")))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.sort", is("createdAt")))
                .andExpect(jsonPath("$.direction", is("DESC")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(importJobService).getPaginatedImportJobs(eq(null), eq("YouTube"), eq(0), eq(10), eq("createdAt"), eq("DESC"));
    }

    @Test
    void getAllImportJobs_WithCustomPaginationAndSorting_ReturnsCustomPagedResponse() throws Exception {
        List<ImportJobDto> jobs = Arrays.asList(testImportJobDto);
        Page<ImportJobDto> page = new PageImpl<>(jobs, PageRequest.of(1, 5), 6);
        when(importJobService.getPaginatedImportJobs(eq(null), eq(null), eq(1), eq(5), eq("status"), eq("ASC"))).thenReturn(Pagination.from(page, "status", "ASC"));

        mockMvc.perform(get("/import-jobs?page=1&size=5&sort=status&direction=ASC")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.sort", is("status")))
                .andExpect(jsonPath("$.direction", is("ASC")))
                .andExpect(jsonPath("$.totalElements", is(6)));

        verify(importJobService).getPaginatedImportJobs(eq(null), eq(null), eq(1), eq(5), eq("status"), eq("ASC"));
    }
}
