package com.pfe.docgen.version;

import com.pfe.docgen.dto.DocumentVersionResponseDTO;
import com.pfe.docgen.dto.VersionDiffResponseDTO;
import com.pfe.docgen.security.CurrentUserService;
import com.pfe.docgen.service.DocumentGenerationService;
import com.pfe.docgen.template.TemplateConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentVersionServiceImpl implements DocumentVersionService {

    private final DocumentVersionRepository repository;
    private final ObjectMapper objectMapper;
    private final DocumentGenerationService documentGenerationService;
    private final DocumentVersionCreationService documentVersionCreationService;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public DocumentVersion createVersion(Long testPlanId,
                                         String format,
                                         byte[] fileContent,
                                         String fileName,
                                         String configSnapshot) {
        return documentVersionCreationService.createVersion(
            testPlanId,
            format,
            fileContent,
            fileName,
            configSnapshot
        );
    }

    @Override
    @Transactional(readOnly = true)
    public VersionDiffResponseDTO compareVersions(Long versionId1, Long versionId2) {
        DocumentVersion version1 = getOwnedVersionOrThrow(versionId1);

        DocumentVersion version2 = getOwnedVersionOrThrow(versionId2);

        if (!Objects.equals(version1.getTestPlanId(), version2.getTestPlanId())) {
            throw new IllegalArgumentException("Versions must belong to the same document/test plan");
        }

        Map<String, Object> snapshot1 = parseSnapshot(version1.getConfigurationSnapshot());
        Map<String, Object> snapshot2 = parseSnapshot(version2.getConfigurationSnapshot());

        Map<String, Object> addedFields = new HashMap<>();
        Map<String, Object> removedFields = new HashMap<>();
        Map<String, Map<String, Object>> changedFields = new HashMap<>();

        for (Map.Entry<String, Object> entry : snapshot2.entrySet()) {
            if (!snapshot1.containsKey(entry.getKey())) {
                addedFields.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, Object> entry : snapshot1.entrySet()) {
            if (!snapshot2.containsKey(entry.getKey())) {
                removedFields.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, Object> entry : snapshot1.entrySet()) {
            String key = entry.getKey();
            if (snapshot2.containsKey(key) && !Objects.equals(entry.getValue(), snapshot2.get(key))) {
                Map<String, Object> change = new HashMap<>();
                change.put("from", entry.getValue());
                change.put("to", snapshot2.get(key));
                changedFields.put(key, change);
            }
        }

        return VersionDiffResponseDTO.builder()
                .addedFields(addedFields)
                .removedFields(removedFields)
                .changedFields(changedFields)
                .build();
    }

    @Override
    @Transactional
    public DocumentVersionResponseDTO restoreVersion(Long versionId) {
        DocumentVersion sourceVersion = getOwnedVersionOrThrow(versionId);

        Long documentId = sourceVersion.getTestPlanId();
        String configSnapshot = sourceVersion.getConfigurationSnapshot();
        String format = sourceVersion.getFileType() != null ? sourceVersion.getFileType() : sourceVersion.getFormat();

        try {
            objectMapper.readValue(configSnapshot, TemplateConfig.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid configuration snapshot JSON", e);
        }

        DocumentVersion restoredVersion = documentGenerationService
                .generateFromSnapshot(documentId, format, configSnapshot);

        return toResponseDTO(restoredVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentVersionResponseDTO> getVersions(Long testPlanId) {
        String username = getRequiredCurrentUsername();
        return repository.findByTestPlanIdAndUserUsernameOrderByVersionNumberDesc(testPlanId, username)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentVersion getVersionContent(Long versionId) {
        return getOwnedVersionOrThrow(versionId);
    }

    private DocumentVersionResponseDTO toResponseDTO(DocumentVersion version) {
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

    private Map<String, Object> parseSnapshot(String configSnapshot) {
        if (configSnapshot == null || configSnapshot.isBlank()) {
            return new HashMap<>();
        }

        try {
            return new HashMap<>(objectMapper.readValue(configSnapshot, Map.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid configuration snapshot JSON", e);
        }
    }

    private DocumentVersion getOwnedVersionOrThrow(Long versionId) {
        String username = getRequiredCurrentUsername();
        return repository.findByIdAndUserUsername(versionId, username)
                .orElseGet(() -> {
                    if (repository.existsById(versionId)) {
                        throw new AccessDeniedException("You do not have access to this document");
                    }
                    throw new VersionNotFoundException(versionId);
                });
    }

    private String getRequiredCurrentUsername() {
        String username = currentUserService.getCurrentUsername();
        if (username == null || username.isBlank()) {
            throw new AccessDeniedException("Unauthenticated access");
        }
        return username;
    }
}