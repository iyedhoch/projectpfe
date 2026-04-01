package com.pfe.docgen.version;

import com.pfe.docgen.entity.DocumentMetadata;
import com.pfe.docgen.entity.TestPlan;
import com.pfe.docgen.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_plan_id", nullable = false)
    private TestPlan testPlan;

    private int versionNumber;

    private String format;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private LocalDateTime generatedAt;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] fileContent;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String configurationSnapshot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_metadata_id")
    private DocumentMetadata documentMetadata;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by_user_id")
    private User generatedBy;
}