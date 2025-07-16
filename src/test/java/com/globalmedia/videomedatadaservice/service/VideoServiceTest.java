package com.globalmedia.videomedatadaservice.service;

import com.globalmedia.videomedatadaservice.domain.model.Video;
import com.globalmedia.videomedatadaservice.repository.VideoRepository;
import com.globalmedia.videomedatadaservice.domain.dto.video.VideoDto;
import com.globalmedia.videomedatadaservice.domain.mapper.VideoMapper;
import com.globalmedia.videomedatadaservice.exception.VideoNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private VideoMapper videoMapper;

    @InjectMocks
    private VideoService videoService;

    private Video testVideo;
    private VideoDto testVideoDto;

    @BeforeEach
    void setUp() {
        testVideo = Video.builder()
                .id(1L)
                .title("Test Video")
                .description("Test Description")
                .url("https://example.com/video")
                .source("YouTube")
                .uploadDate(LocalDate.now())
                .durationInSeconds(120)
                .createdAt(LocalDateTime.now())
                .build();

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
    void getVideoById_ExistingId_ReturnsVideo() {
        when(videoRepository.findById(1L)).thenReturn(Optional.of(testVideo));
        when(videoMapper.toDto(testVideo)).thenReturn(testVideoDto);

        VideoDto result = videoService.getVideoById(1L);

        assertNotNull(result);
        assertEquals(testVideoDto.getId(), result.getId());
        assertEquals(testVideoDto.getTitle(), result.getTitle());

        verify(videoRepository).findById(1L);
        verify(videoMapper).toDto(testVideo);
    }

    @Test
    void getVideoById_NonExistingId_ThrowsException() {
        when(videoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(VideoNotFoundException.class, () -> {
            videoService.getVideoById(999L);
        });

        verify(videoRepository).findById(999L);
        verifyNoInteractions(videoMapper);
    }

    @Test
    void getAllVideos_ReturnsAllVideos() {
        List<Video> videos = Arrays.asList(testVideo);
        List<VideoDto> videoDtos = Arrays.asList(testVideoDto);

        when(videoRepository.findAll()).thenReturn(videos);
        when(videoMapper.toDtoList(videos)).thenReturn(videoDtos);

        List<VideoDto> result = videoService.getAllVideos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testVideoDto.getId(), result.get(0).getId());

        verify(videoRepository).findAll();
        verify(videoMapper).toDtoList(videos);
    }
}
