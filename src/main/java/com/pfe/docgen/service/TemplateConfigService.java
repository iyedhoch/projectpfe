package com.pfe.docgen.service;

import com.pfe.docgen.filter.DocumentFilter;
import com.pfe.docgen.template.TemplateConfig;

public interface TemplateConfigService {

    DocumentFilter buildFilterFromTemplate(String templateName);

    TemplateConfig getByName(String name);

    TemplateConfig createTemplate(TemplateConfig template);

    boolean existsByName(String name);
}
