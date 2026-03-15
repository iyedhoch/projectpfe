package com.pfe.docgen.version;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentVersionRepository
        extends JpaRepository<DocumentVersion, Long> {

    List<DocumentVersion> findByTestPlanIdOrderByVersionNumberDesc(Long testPlanId);

    List<DocumentVersion> findByTestPlanIdAndGeneratedByUsernameOrderByVersionNumberDesc(Long testPlanId, String username);

    Optional<DocumentVersion> findTopByTestPlanIdOrderByVersionNumberDesc(Long testPlanId);

    Optional<DocumentVersion> findTopByTestPlanIdAndGeneratedByUsernameOrderByVersionNumberDesc(Long testPlanId, String username);

    Optional<DocumentVersion> findByIdAndGeneratedByUsername(Long id, String username);
}