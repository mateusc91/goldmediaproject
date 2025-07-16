package com.globalmedia.videometadataservice.web.controller;

import com.globalmedia.videometadataservice.controller.VideoController;
import com.globalmedia.videometadataservice.service.VideoService;
import com.globalmedia.videometadataservice.domain.model.Pagination;
import com.globalmedia.videometadataservice.domain.dto.VideoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VideoControllerTest {

    @Mock
    private VideoService videoService;

    @InjectMocks
    private VideoController videoController;

    private MockMvc mockMvc;
    private VideoDto testVideoDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(videoController).build();

        testVideoDto = VideoDto.builder()
                .id(1L)
                .title("Test Video")
                .description("Test Description")
                .url("https://example.com/video")
                .source("YouTube")
                .uploadDate(LocalDate.now())
                .durationInSeconds(120)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getVideoById_ExistingId_ReturnsVideo() throws Exception {
        when(videoService.getVideoById(1L)).thenReturn(testVideoDto);

        mockMvc.perform(get("/videos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Video")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.source", is("YouTube")));

        verify(videoService).getVideoById(1L);
    }

    @Test
    void getAllVideos_ReturnsPagedResponse() throws Exception {
        List<VideoDto> videos = Arrays.asList(testVideoDto);
        Pagination<VideoDto> pagination = new Pagination<>(videos, 0, 10, "id", "ASC", 1);
        when(videoService.getPaginatedVideos(eq(null), eq(null), eq(null), eq(null), eq(null), eq(0), eq(10), eq("id"), eq("ASC"))).thenReturn(pagination);

        mockMvc.perform(get("/videos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Test Video")))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.sort", is("id")))
                .andExpect(jsonPath("$.direction", is("ASC")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(videoService).getPaginatedVideos(eq(null), eq(null), eq(null), eq(null), eq(null), eq(0), eq(10), eq("id"), eq("ASC"));
    }

    @Test
    void getAllVideos_WithSourceFilter_ReturnsFilteredPagedResponse() throws Exception {
        List<VideoDto> videos = Arrays.asList(testVideoDto);
        Pagination<VideoDto> pagination = new Pagination<>(videos, 0, 10, "id", "ASC", 1);
        when(videoService.getPaginatedVideos(eq("YouTube"), eq(null), eq(null), eq(null), eq(null), eq(0), eq(10), eq("id"), eq("ASC"))).thenReturn(pagination);

        mockMvc.perform(get("/videos?source=YouTube")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].source", is("YouTube")))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.sort", is("id")))
                .andExpect(jsonPath("$.direction", is("ASC")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(videoService).getPaginatedVideos(eq("YouTube"), eq(null), eq(null), eq(null), eq(null), eq(0), eq(10), eq("id"), eq("ASC"));
    }

    @Test
    void getAllVideos_WithCustomPaginationAndSorting_ReturnsCustomPagedResponse() throws Exception {
        List<VideoDto> videos = Arrays.asList(testVideoDto);
        Pagination<VideoDto> pagination = new Pagination<>(videos, 1, 5, "title", "DESC", 6);
        when(videoService.getPaginatedVideos(eq(null), eq(null), eq(null), eq(null), eq(null), eq(1), eq(5), eq("title"), eq("DESC"))).thenReturn(pagination);

        mockMvc.perform(get("/videos?page=1&size=5&sort=title&direction=DESC")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.sort", is("title")))
                .andExpect(jsonPath("$.direction", is("DESC")))
                .andExpect(jsonPath("$.totalElements", is(6)));

        verify(videoService).getPaginatedVideos(eq(null), eq(null), eq(null), eq(null), eq(null), eq(1), eq(5), eq("title"), eq("DESC"));
    }
}
