package com.pfe.docgen.controller;
import com.pfe.docgen.dto.DocumentVersionResponseDTO;
import com.pfe.docgen.dto.VersionDiffResponseDTO;
import com.pfe.docgen.version.DocumentVersion;
import com.pfe.docgen.version.DocumentVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/versions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Document Versions", description = "Document version management, comparison, and restoration endpoints")
public class DocumentVersionController {

    private final DocumentVersionService documentVersionService;

    @GetMapping("/{testPlanId}")
    @Operation(summary = "Get document versions", description = "Retrieve all versions of a document for a specific test plan")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Versions retrieved successfully",
            content = @Content(schema = @Schema(implementation = DocumentVersionResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Test plan not found")
    })
    public List<DocumentVersionResponseDTO> getVersions(@PathVariable Long testPlanId) {
    return documentVersionService.getVersions(testPlanId);
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "Download a document version", description = "Download a specific version of a document in its stored format")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Version not found")
    })
    public ResponseEntity<byte[]> downloadVersion(@PathVariable Long id) {
    DocumentVersion version = documentVersionService.getVersionContent(id);
    String contentType = getContentTypeFromStored(version.getFileType());

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + version.getFileName() + "\"")
        .contentType(MediaType.parseMediaType(contentType))
        .contentLength(version.getFileSize())
        .body(version.getFileContent());
    }

    @GetMapping("/compare")
    @Operation(summary = "Compare two document versions", description = "Compare two versions of a document and get the differences")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comparison successful",
            content = @Content(schema = @Schema(implementation = VersionDiffResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "One or both versions not found")
    })
    public VersionDiffResponseDTO compareVersions(@RequestParam("version1") Long versionId1,
                                                  @RequestParam("version2") Long versionId2) {
        return documentVersionService.compareVersions(versionId1, versionId2);
    }

    @PostMapping("/{versionId}/restore")
    @Operation(summary = "Restore a document version", description = "Restore a previous version of a document as the current version")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Version restored successfully",
            content = @Content(schema = @Schema(implementation = DocumentVersionResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Version not found")
    })
    public DocumentVersionResponseDTO restoreVersion(@PathVariable Long versionId) {
        return documentVersionService.restoreVersion(versionId);
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

