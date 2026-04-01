package com.pfe.docgen.service;

import com.pfe.docgen.document.DocumentStatistics;
import com.pfe.docgen.document.DocumentTestPlan;
import com.pfe.docgen.dto.TestPlanDTO;
import com.pfe.docgen.export.ExcelExportService;
import com.pfe.docgen.export.PdfExportService;
import com.pfe.docgen.export.WordExportService;
import com.pfe.docgen.filter.DocumentFilter;
import com.pfe.docgen.filter.DocumentFilterService;
import com.pfe.docgen.mapper.DocumentMapper;
import com.pfe.docgen.statistics.StatisticsService;
import com.pfe.docgen.template.TemplateConfig;
import com.pfe.docgen.version.DocumentVersion;
import com.pfe.docgen.version.DocumentVersionCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import tools.jackson.databind.ObjectMapper;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentGenerationServiceImpl implements DocumentGenerationService {

    private final TestPlanService testPlanService;
    private final DocumentMapper documentMapper;
    private final DocumentFilterService filterService;
    private final TemplateConfigService templateConfigService;
    private final StatisticsService statisticsService;

    private final SpringTemplateEngine templateEngine;
    private final PdfExportService pdfExportService;
    private final WordExportService wordExportService;
    private final ExcelExportService excelExportService;
    private final DocumentVersionCreationService documentVersionCreationService;
    private final ObjectMapper objectMapper;

    /**
     * Build document with optional filtering based on template
     */
    private GeneratedDocumentData buildDocument(Long testPlanId, String templateName) {

        // 1. Get test plan DTO
        TestPlanDTO dto = testPlanService.getTestPlanById(testPlanId);
        if (dto == null) {
            throw new RuntimeException("Test plan not found with id: " + testPlanId);
        }

        // 2. Map to document model
        DocumentTestPlan document = documentMapper.toDocumentModel(dto);

        // 3. Get configuration based on template name or active config
        TemplateConfig config;
        if (templateName != null && !templateName.isBlank()) {
            config = templateConfigService.getByName(templateName);
        } else {
            config = templateConfigService.getActiveConfig();
        }

        return buildDocument(testPlanId, config);
    }

    private GeneratedDocumentData buildDocument(Long testPlanId, TemplateConfig config) {
        TestPlanDTO dto = testPlanService.getTestPlanById(testPlanId);
        if (dto == null) {
            throw new RuntimeException("Test plan not found with id: " + testPlanId);
        }

        DocumentTestPlan document = documentMapper.toDocumentModel(dto);

        if (config == null) {
            throw new RuntimeException("No template configuration found");
        }

        log.debug("Using template: {} with onlyFailed={}", config.getName(), config.isOnlyFailed());

        if (config.isOnlyFailed()) {
            document = filterService.filterOnlyFailed(document);
        }

        DocumentStatistics statistics = null;
        if (config.isShowStatistics() && document != null) {
            statistics = statisticsService.computeStatistics(document);
        }

        return new GeneratedDocumentData(document, config, statistics);
    }

    @Override
    public String generateHtmlDocument(Long testPlanId, String templateName) {

        // Use the shared buildDocument method to avoid duplication
        GeneratedDocumentData data = buildDocument(testPlanId, templateName);

        // Build Thymeleaf context
        Context context = new Context();
        context.setVariable("document", data.document());
        context.setVariable("config", data.config());
        context.setVariable("statistics", data.statistics());

        // Add additional useful variables
        context.setVariable("generationDate", java.time.LocalDateTime.now());
        context.setVariable("format", "HTML");

        // Render template
        String html = templateEngine.process("document-template", context);

        // Generate dynamic filename
        String fileName = generateFileName(data.document().getTitle(), "html");

        // Save version record (convert HTML string to bytes)
        documentVersionCreationService.createVersion(
                testPlanId,
                "HTML",
                html.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                fileName,
                convertConfigToJson(data.config())
        );

        return html;
    }

    @Override
    public byte[] generatePdfDocument(Long testPlanId, String templateName) {

        GeneratedDocumentData data = buildDocument(testPlanId, templateName);

        Context context = new Context();
        context.setVariable("document", data.document());
        context.setVariable("config", data.config());
        context.setVariable("statistics", data.statistics());
        context.setVariable("generationDate", java.time.LocalDateTime.now());
        context.setVariable("format", "PDF");

        String html = templateEngine.process("document-template", context);

        byte[] pdf = pdfExportService.generatePdfFromHtml(html);

        // Generate dynamic filename
        String fileName = generateFileName(data.document().getTitle(), "pdf");

        documentVersionCreationService.createVersion(
                testPlanId,
                "PDF",
                pdf,
                fileName,
                convertConfigToJson(data.config())
        );

        return pdf;
    }

    @Override
    public byte[] generateWordDocument(Long testPlanId, String templateName) {

        GeneratedDocumentData data = buildDocument(testPlanId, templateName);

        byte[] word = wordExportService.generateWord(data.document());

        // Generate dynamic filename
        String fileName = generateFileName(data.document().getTitle(), "docx");

        documentVersionCreationService.createVersion(
                testPlanId,
                "WORD",
                word,
                fileName,
                convertConfigToJson(data.config())
        );

        return word;
    }

    @Override
    public byte[] generateExcelDocument(Long testPlanId, String templateName) {

        GeneratedDocumentData data = buildDocument(testPlanId, templateName);

        byte[] excel = excelExportService.generateExcel(data.document());

        // Generate dynamic filename
        String fileName = generateFileName(data.document().getTitle(), "xlsx");

        documentVersionCreationService.createVersion(
                testPlanId,
                "EXCEL",
                excel,
                fileName,
                convertConfigToJson(data.config())
        );

        return excel;
    }

    @Override
    public DocumentVersion generateFromSnapshot(Long documentId, String format, String configSnapshot) {
        try {
            TemplateConfig snapshotConfig = objectMapper.readValue(configSnapshot, TemplateConfig.class);
            GeneratedDocumentData data = buildDocument(documentId, snapshotConfig);

            String upperFormat = format == null ? "PDF" : format.toUpperCase();
            String extension;
            byte[] generatedBytes;

            switch (upperFormat) {
                case "HTML" -> {
                    Context context = new Context();
                    context.setVariable("document", data.document());
                    context.setVariable("config", data.config());
                    context.setVariable("statistics", data.statistics());
                    context.setVariable("generationDate", java.time.LocalDateTime.now());
                    context.setVariable("format", "HTML");

                    String html = templateEngine.process("document-template", context);
                    extension = "html";
                    generatedBytes = html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                }
                case "WORD" -> {
                    extension = "docx";
                    generatedBytes = wordExportService.generateWord(data.document());
                }
                case "EXCEL" -> {
                    extension = "xlsx";
                    generatedBytes = excelExportService.generateExcel(data.document());
                }
                case "PDF" -> {
                    Context context = new Context();
                    context.setVariable("document", data.document());
                    context.setVariable("config", data.config());
                    context.setVariable("statistics", data.statistics());
                    context.setVariable("generationDate", java.time.LocalDateTime.now());
                    context.setVariable("format", "PDF");

                    String html = templateEngine.process("document-template", context);
                    extension = "pdf";
                    generatedBytes = pdfExportService.generatePdfFromHtml(html);
                }
                default -> throw new IllegalArgumentException("Unsupported format for restore: " + format);
            }

            String fileName = generateFileName(data.document().getTitle(), extension);

                return documentVersionCreationService.createVersion(
                    documentId,
                    upperFormat,
                    generatedBytes,
                    fileName,
                    configSnapshot
            );
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate document from snapshot", e);
        }
    }

    /**
     * Generate dynamic filename based on document title and extension
     */
    private String generateFileName(String documentTitle, String extension) {
        if (documentTitle == null || documentTitle.isBlank()) {
            documentTitle = "document";
        }
        // Sanitize filename: remove special characters, replace spaces with underscores
        String sanitized = documentTitle
                .replaceAll("[^a-zA-Z0-9\\s-]", "")
                .replaceAll("\\s+", "_")
                .toLowerCase();
        
        return sanitized + "." + extension;
    }

    /**
     * Convert config to JSON for version history
     */
    private String convertConfigToJson(TemplateConfig config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            log.error("Failed to serialize config: {}", config.getName(), e);
            return "{\"error\": \"Failed to serialize config\"}";
        }
    }

    /**
     * Record to hold generated document data
     */
    private record GeneratedDocumentData(
            DocumentTestPlan document,
            TemplateConfig config,
            DocumentStatistics statistics
    ) {}
}