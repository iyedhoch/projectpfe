package com.pfe.docgen.controller;
import com.pfe.docgen.dto.DocumentVersionResponseDTO;
import com.pfe.docgen.dto.VersionDiffResponseDTO;
import com.pfe.docgen.version.DocumentVersion;
import com.pfe.docgen.version.DocumentVersionRepository;
import com.pfe.docgen.version.DocumentVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/versions")
@RequiredArgsConstructor
public class DocumentVersionController {

    private final DocumentVersionRepository repository;
    private final DocumentVersionService documentVersionService;

    @GetMapping("/{testPlanId}")
    public List<DocumentVersionResponseDTO> getVersions(@PathVariable Long testPlanId) {
        return repository.findByTestPlanIdOrderByVersionNumberDesc(testPlanId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadVersion(@PathVariable Long id) {
        return repository.findById(id)
                .map(version -> {
                    String contentType = getContentTypeFromStored(version.getFileType());
                    
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, 
                                    "attachment; filename=\"" + version.getFileName() + "\"")
                            .contentType(MediaType.parseMediaType(contentType))
                            .contentLength(version.getFileSize())
                            .body(version.getFileContent());
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/compare")
    public VersionDiffResponseDTO compareVersions(@RequestParam("version1") Long versionId1,
                                                  @RequestParam("version2") Long versionId2) {
        return documentVersionService.compareVersions(versionId1, versionId2);
    }

    @PostMapping("/{versionId}/restore")
    public DocumentVersionResponseDTO restoreVersion(@PathVariable Long versionId) {
        return documentVersionService.restoreVersion(versionId);
    }

    /**
     * Map entity to DTO (excludes file content)
     */
    private DocumentVersionResponseDTO toDTO(DocumentVersion version) {
        return DocumentVersionResponseDTO.builder()
                .id(version.getId())
                .versionNumber(version.getVersionNumber())
                .fileName(version.getFileName())
                .fileType(version.getFileType())
                .fileSize(version.getFileSize())
                .generatedAt(version.getGeneratedAt())
                .format(version.getFormat())
                .build();
    }

    /**
     * Get Content-Type from stored file type
     */
    private String getContentTypeFromStored(String fileType) {
        if (fileType == null) {
            return "application/octet-stream";
        }
        
        return switch (fileType.toUpperCase()) {
            case "PDF" -> "application/pdf";
            case "WORD" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "EXCEL" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "HTML" -> "text/html";
            default -> "application/octet-stream";
        };
    }
}

