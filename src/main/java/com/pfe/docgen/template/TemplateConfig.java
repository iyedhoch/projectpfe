package com.pfe.docgen.template;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "template_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    private String executionStatusFilter;

    private boolean includeExecutions;

    private boolean includeExpectedResults;

    private boolean includeStatistics;

    private String customTitlePrefix;
}

