package com.pfe.docgen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for returning document version metadata without file content
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentVersionResponseDTO {

    private Long id;

    private Integer versionNumber;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private LocalDateTime generatedAt;

    private String format;
}
