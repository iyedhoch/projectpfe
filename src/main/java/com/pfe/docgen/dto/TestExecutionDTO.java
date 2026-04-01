package com.pfe.docgen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestExecutionDTO {

    private Long id;
    private String status;
    private String comment;
    private LocalDateTime date;
}
