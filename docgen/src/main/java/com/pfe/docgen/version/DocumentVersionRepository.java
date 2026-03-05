package com.pfe.docgen.version;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentVersionRepository
        extends JpaRepository<DocumentVersion, Long> {

    List<DocumentVersion> findByTestPlanIdOrderByVersionNumberDesc(Long testPlanId);

    List<DocumentVersion> findByTestPlanIdAndUserUsernameOrderByVersionNumberDesc(Long testPlanId, String username);

    Optional<DocumentVersion> findTopByTestPlanIdOrderByVersionNumberDesc(Long testPlanId);

    Optional<DocumentVersion> findTopByTestPlanIdAndUserUsernameOrderByVersionNumberDesc(Long testPlanId, String username);

    Optional<DocumentVersion> findByIdAndUserUsername(Long id, String username);
}