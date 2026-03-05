package com.pfe.docgen.service;

import com.pfe.docgen.filter.DocumentFilter;
import com.pfe.docgen.repository.TemplateConfigRepository;
import com.pfe.docgen.template.TemplateConfig;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateConfigServiceImpl implements TemplateConfigService {

    private final TemplateConfigRepository templateconfigrepository;

    @Override
    public TemplateConfig getByName(String name) {
        return templateconfigrepository.findByName(name)
                .orElseThrow(() ->
                        new RuntimeException("Template not found: " + name)
                );
    }

    @Override
    public boolean existsByName(String name) {
        return templateconfigrepository.findByName(name).isPresent();
    }

    @Override
    public TemplateConfig createTemplate(TemplateConfig template) {

        if (existsByName(template.getName())) {
            return getByName(template.getName());
        }

        return templateconfigrepository.save(template);
    }

    @Override
    public DocumentFilter buildFilterFromTemplate(String templateName) {

        if (templateName == null || templateName.isBlank()) {
            return null;
        }

        TemplateConfig config = getByName(templateName);

        return DocumentFilter.builder()
                .executionStatus(config.getExecutionStatusFilter())
                .includeExecutions(config.isIncludeExecutions())
                .includeExpectedResults(config.isIncludeExpectedResults())
                .build();
    }

    @Override
    public TemplateConfig getActiveConfig() {
        return templateconfigrepository.findByActiveTrue()
                .orElseThrow(() -> new RuntimeException("No active template config found"));
    }

    @Override
    @Transactional
    public TemplateConfig save(TemplateConfig config) {
        templateconfigrepository.deactivateAll();
        config.setActive(true);
        return templateconfigrepository.save(config);
    }

    @Override
    public java.util.List<TemplateConfig> getAllTemplates() {
        return templateconfigrepository.findAll();
    }
}

