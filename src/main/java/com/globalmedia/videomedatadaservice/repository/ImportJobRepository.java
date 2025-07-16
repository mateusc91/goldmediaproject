package com.globalmedia.videomedatadaservice.repository;

import com.globalmedia.videomedatadaservice.domain.model.ImportJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportJobRepository extends JpaRepository<ImportJob, Long> {
    
    List<ImportJob> findByStatus(ImportJob.ImportStatus status);
    
    Page<ImportJob> findByStatus(ImportJob.ImportStatus status, Pageable pageable);
    
    List<ImportJob> findBySource(String source);
    
    Page<ImportJob> findBySource(String source, Pageable pageable);
}