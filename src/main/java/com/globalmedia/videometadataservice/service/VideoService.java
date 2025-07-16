package com.globalmedia.videometadataservice.service;

import com.globalmedia.videometadataservice.domain.model.Pagination;
import com.globalmedia.videometadataservice.domain.request.VideoCreateRequest;
import com.globalmedia.videometadataservice.domain.dto.VideoDto;
import com.globalmedia.videometadataservice.domain.dto.VideoStatisticsDto;
import com.globalmedia.videometadataservice.domain.mapper.VideoMapper;
import com.globalmedia.videometadataservice.domain.model.Video;
import com.globalmedia.videometadataservice.exception.VideoNotFoundException;
import com.globalmedia.videometadataservice.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;

    @Transactional(readOnly = true)
    public List<VideoDto> getAllVideos() {
        return videoMapper.toDtoList(videoRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Page<VideoDto> getAllVideos(Pageable pageable) {
        Page<Video> videoPage = videoRepository.findAll(pageable);
        return videoPage.map(videoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<VideoDto> getVideosBySource(String source, Pageable pageable) {
        Page<Video> videoPage = videoRepository.findBySource(source, pageable);
        return videoPage.map(videoMapper::toDto);
    }


    @Transactional(readOnly = true)
    public Page<VideoDto> getVideosByUploadDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Video> videoPage = videoRepository.findByUploadDateBetween(startDate, endDate, pageable);
        return videoPage.map(videoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<VideoDto> getVideosByDurationRange(Integer minDuration, Integer maxDuration, Pageable pageable) {
        Page<Video> videoPage = videoRepository.findByDurationInSecondsBetween(minDuration, maxDuration, pageable);
        return videoPage.map(videoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public VideoDto getVideoById(Long id) {
        return videoRepository.findById(id)
                .map(videoMapper::toDto)
                .orElseThrow(() -> new VideoNotFoundException(id));
    }

    @Transactional
    @CacheEvict(value = "videoStatistics", allEntries = true)
    public VideoDto createVideo(VideoCreateRequest request) {
        Video video = videoMapper.toEntity(request);
        Video savedVideo = videoRepository.save(video);
        return videoMapper.toDto(savedVideo);
    }

    @Transactional
    @CacheEvict(value = "videoStatistics", allEntries = true)
    public List<VideoDto> importVideos(List<VideoCreateRequest> requests) {
        List<Video> videos = requests.stream()
                .map(videoMapper::toEntity)
                .collect(Collectors.toList());

        List<Video> savedVideos = videoRepository.saveAll(videos);
        log.info("Imported {} videos", savedVideos.size());

        return videoMapper.toDtoList(savedVideos);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "videoStatistics")
    public VideoStatisticsDto getVideoStatistics() {
        List<Object[]> countBySource = videoRepository.countVideosBySource();
        Map<String, Long> videoCountBySource = new HashMap<>();

        for (Object[] result : countBySource) {
            String source = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            videoCountBySource.put(source, count);
        }

        List<Object[]> avgDurationBySource = videoRepository.averageDurationBySource();
        Map<String, String> averageDurationBySource = new HashMap<>();

        for (Object[] result : avgDurationBySource) {
            String source = (String) result[0];
            Double avgDurationSeconds = (Double) result[1];

            if (avgDurationSeconds != null) {
                int hours = (int) (avgDurationSeconds / 3600);
                int minutes = (int) ((avgDurationSeconds % 3600) / 60);
                int seconds = (int) (avgDurationSeconds % 60);

                String formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                averageDurationBySource.put(source, formattedDuration);
            } else {
                averageDurationBySource.put(source, "00:00:00");
            }
        }

        long totalVideos = videoCountBySource.values().stream().mapToLong(Long::longValue).sum();

        return VideoStatisticsDto.builder()
                .videoCountBySource(videoCountBySource)
                .averageDurationBySource(averageDurationBySource)
                .totalVideos(totalVideos)
                .build();
    }

    @Transactional(readOnly = true)
    public Pagination<VideoDto> getPaginatedVideos(String source, LocalDate startDate, LocalDate endDate,
                                                   Integer minDuration, Integer maxDuration,
                                                   int page, int size, String sort, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<VideoDto> resultPage;

        if (source != null) {
            resultPage = getVideosBySource(source, pageable);
        } else if (startDate != null && endDate != null) {
            resultPage = getVideosByUploadDateRange(startDate, endDate, pageable);
        } else if (minDuration != null && maxDuration != null) {
            resultPage = getVideosByDurationRange(minDuration, maxDuration, pageable);
        } else {
            resultPage = getAllVideos(pageable);
        }

        return Pagination.from(resultPage, sort, direction);
    }
}
