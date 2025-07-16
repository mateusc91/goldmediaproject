package com.globalmedia.videomedatadaservice.repository;

import com.globalmedia.videomedatadaservice.domain.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    Page<Video> findBySource(String source, Pageable pageable);

    Page<Video> findByUploadDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Video> findByDurationInSecondsBetween(Integer minDuration, Integer maxDuration, Pageable pageable);

    List<Video> findBySource(String source);

    List<Video> findByUploadDateBetween(LocalDate startDate, LocalDate endDate);

    List<Video> findByDurationInSecondsBetween(Integer minDuration, Integer maxDuration);

    @Query("SELECT v.source, COUNT(v) FROM Video v GROUP BY v.source")
    List<Object[]> countVideosBySource();

    @Query("SELECT v.source, AVG(v.durationInSeconds) FROM Video v GROUP BY v.source")
    List<Object[]> averageDurationBySource();
}
