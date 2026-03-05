package com.pfe.docgen.version;

import com.pfe.docgen.dto.DocumentVersionResponseDTO;
import com.pfe.docgen.dto.VersionDiffResponseDTO;

import java.util.List;

public interface DocumentVersionService {

    DocumentVersion createVersion(Long testPlanId,
                                  String format,
                                  byte[] fileContent,
                                  String fileName,
                                  String configSnapshot);

    VersionDiffResponseDTO compareVersions(Long versionId1, Long versionId2);

    DocumentVersionResponseDTO restoreVersion(Long versionId);

    List<DocumentVersionResponseDTO> getVersions(Long testPlanId);

    DocumentVersion getVersionContent(Long versionId);
}