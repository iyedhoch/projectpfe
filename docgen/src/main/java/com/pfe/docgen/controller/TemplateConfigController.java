package com.pfe.docgen.controller;

import com.pfe.docgen.service.TemplateConfigService;
import com.pfe.docgen.template.TemplateConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class TemplateConfigController {

    private final TemplateConfigService templateConfigService;

    @GetMapping
    public TemplateConfig getActiveConfig() {
        return templateConfigService.getActiveConfig();
    }

    @GetMapping("/all")
    public List<TemplateConfig> getAllTemplates() {
        return templateConfigService.getAllTemplates();
    }

    @PostMapping
    public TemplateConfig updateConfig(@RequestBody TemplateConfig config) {
        return templateConfigService.save(config);
    }
}