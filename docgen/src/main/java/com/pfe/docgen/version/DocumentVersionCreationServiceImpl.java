package com.pfe.docgen.version;

import com.pfe.docgen.security.CurrentUserService;
import com.pfe.docgen.user.User;
import com.pfe.docgen.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DocumentVersionCreationServiceImpl implements DocumentVersionCreationService {

    private final DocumentVersionRepository repository;
        private final UserRepository userRepository;
        private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public DocumentVersion createVersion(Long testPlanId,
                                         String format,
                                         byte[] fileContent,
                                         String fileName,
                                         String configSnapshot) {

        String username = currentUserService.getCurrentUsername();
        if (username == null) {
            throw new AccessDeniedException("Unauthenticated access");
        }

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found"));

        int nextVersion = repository
                .findTopByTestPlanIdAndUserUsernameOrderByVersionNumberDesc(testPlanId, username)
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
                .user(currentUser)
                .build();

        return repository.save(version);
    }
}