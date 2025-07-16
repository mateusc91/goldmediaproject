package com.globalmedia.videomedatadaservice.domain.mapper;

import com.globalmedia.videomedatadaservice.domain.model.ImportJob;
import com.globalmedia.videomedatadaservice.domain.dto.importjob.ImportJobDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImportJobMapper {

    ImportJobDto toDto(ImportJob importJob);

    List<ImportJobDto> toDtoList(List<ImportJob> importJobs);
}