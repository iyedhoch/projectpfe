package com.pfe.docgen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "document_metadata")
public class DocumentMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentTitle;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String preparedBy;

    private String preparedFor;

    private String author;

    private String qaTeam;

    private LocalDate documentDate;

    private String description;

    private String purpose;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_plan_id")
    private TestPlan testPlan;

    @OneToMany(mappedBy = "documentMetadata", cascade = CascadeType.ALL)
    private List<DocumentRevision> revisions;

    @OneToMany(mappedBy = "documentMetadata", cascade = CascadeType.ALL)
    private List<Approval> approvals;
}
