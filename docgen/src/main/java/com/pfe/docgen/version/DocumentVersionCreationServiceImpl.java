package com.pfe.docgen.version;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DocumentVersionCreationServiceImpl implements DocumentVersionCreationService {

    private final DocumentVersionRepository repository;

    @Override
    @Transactional
    public DocumentVersion createVersion(Long testPlanId,
                                         String format,
                                         byte[] fileContent,
                                         String fileName,
                                         String configSnapshot) {

        int nextVersion = repository
                .findTopByTestPlanIdOrderByVersionNumberDesc(testPlanId)
                .map(v -> v.getVersionNumber() + 1)
                .orElse(1);

        DocumentVersion version = DocumentVersion.builder()
                .testPlanId(testPlanId)
                .versionNumber(nextVersion)
                .format(format)
                .fileName(fileName)
                .fileType(format)
                .fileSize((long) fileContent.length)
                .generatedAt(LocalDateTime.now())
                .fileContent(fileContent)
                .configurationSnapshot(configSnapshot)
                .build();

        return repository.save(version);
    }
}