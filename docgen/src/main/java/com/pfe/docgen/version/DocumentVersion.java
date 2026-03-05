package com.pfe.docgen.version;

import com.pfe.docgen.template.TemplateConfig;
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

    private Long testPlanId;

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
}