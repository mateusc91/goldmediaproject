package com.globalmedia.videomedatadaservice.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    
    private List<T> content;
    private int page;
    private int size;
    private String sort;
    private String direction;
    private long totalElements;

    public static <T> PagedResponse<T> from(Page<T> page, String sortField, String direction) {
        return PagedResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .sort(sortField)
                .direction(direction)
                .totalElements(page.getTotalElements())
                .build();
    }
}