package com.pfe.docgen.service;

import com.pfe.docgen.filter.DocumentFilter;
import com.pfe.docgen.repository.TemplateConfigRepository;
import com.pfe.docgen.template.TemplateConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateConfigServiceImpl implements TemplateConfigService {

    private final TemplateConfigRepository repository;

    @Override
    public TemplateConfig getByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() ->
                        new RuntimeException("Template not found: " + name)
                );
    }

    @Override
    public boolean existsByName(String name) {
        return repository.findByName(name).isPresent();
    }

    @Override
    public TemplateConfig createTemplate(TemplateConfig template) {

        if (existsByName(template.getName())) {
            return getByName(template.getName());
        }

        return repository.save(template);
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
}

