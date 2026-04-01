package com.pfe.docgen.version;

public interface DocumentVersionCreationService {

    DocumentVersion createVersion(Long testPlanId,
                                  String format,
                                  byte[] fileContent,
                                  String fileName,
                                  String configSnapshot);
}