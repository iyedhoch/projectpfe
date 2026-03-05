package com.pfe.docgen.version;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentVersionRepository
        extends JpaRepository<DocumentVersion, Long> {

    List<DocumentVersion> findByTestPlanIdOrderByVersionNumberDesc(Long testPlanId);

    Optional<DocumentVersion> findTopByTestPlanIdOrderByVersionNumberDesc(Long testPlanId);
}