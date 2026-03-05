package com.pfe.docgen.template;

import com.pfe.docgen.service.TemplateConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TemplateConfigDataLoader implements CommandLineRunner {

    private final TemplateConfigService templateConfigService;

    @Override
    public void run(String... args) {

        // CLIENT REPORT TEMPLATE (Set as active by default)
        templateConfigService.createTemplate(
                TemplateConfig.builder()
                        .name("CLIENT_REPORT")
                        .description("Client-friendly report with only failed tests")
                        .executionStatusFilter("FAILED")
                        .includeExecutions(false)
                        .includeExpectedResults(false)
                        .includeStatistics(false)
                        .customTitlePrefix("Client Report - ")
                        .active(true)  // Set as active template
                        .build()
        );

        // INTERNAL QA TEMPLATE
        templateConfigService.createTemplate(
                TemplateConfig.builder()
                        .name("INTERNAL_QA")
                        .description("Detailed internal QA report")
                        .executionStatusFilter(null)
                        .includeExecutions(true)
                        .includeExpectedResults(true)
                        .includeStatistics(false)
                        .customTitlePrefix("Internal QA - ")
                        .active(false)
                        .build()
        );
    }
}
