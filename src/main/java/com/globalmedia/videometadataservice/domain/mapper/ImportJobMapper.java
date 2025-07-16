package com.globalmedia.videometadataservice.domain.mapper;

import com.globalmedia.videometadataservice.domain.model.ImportJob;
import com.globalmedia.videometadataservice.domain.dto.ImportJobDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImportJobMapper {

    ImportJobDto toDto(ImportJob importJob);

    List<ImportJobDto> toDtoList(List<ImportJob> importJobs);
}