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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@Service
@RequiredArgsConstructor
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

    @Override
    public String generateHtmlDocument(Long testPlanId, String templateName) {

        DocumentTestPlan document =
                prepareFilteredDocument(testPlanId, templateName);

        DocumentStatistics statistics =
                statisticsService.computeStatistics(document);

        TemplateConfig templateConfig =
                resolveTemplateConfig(templateName);

        Context context =
                buildContext(document, statistics, templateConfig);

        return templateEngine.process("document-template", context);
    }

    @Override
    public byte[] generatePdfDocument(Long testPlanId, String templateName) {
        String html = generateHtmlDocument(testPlanId, templateName);
        return pdfExportService.generatePdfFromHtml(html);
    }

    @Override
    public byte[] generateWordDocument(Long testPlanId, String templateName) {
        DocumentTestPlan document =
                prepareFilteredDocument(testPlanId, templateName);
        return wordExportService.generateWord(document);
    }

    @Override
    public byte[] generateExcelDocument(Long testPlanId, String templateName) {
        DocumentTestPlan document =
                prepareFilteredDocument(testPlanId, templateName);
        return excelExportService.generateExcel(document);
    }


    // -----------------------
    // Helper methods
    // -----------------------

    private DocumentTestPlan prepareFilteredDocument(Long id, String templateName) {

        TestPlanDTO dto = testPlanService.getTestPlanById(id);
        DocumentTestPlan document = documentMapper.toDocumentModel(dto);

        var filter = templateConfigService
                .buildFilterFromTemplate(templateName);

        return filterService.applyFilters(document, filter);
    }

    private TemplateConfig resolveTemplateConfig(String templateName) {

        if (templateName == null || templateName.isBlank()) {
            return null;
        }

        return templateConfigService.getByName(templateName);
    }

    private Context buildContext(DocumentTestPlan document,
                                 DocumentStatistics statistics,
                                 TemplateConfig templateConfig) {

        Context context = new Context();

        context.setVariable("title", document.getTitle());
        context.setVariable("build", document.getBuild());
        context.setVariable("scope", document.getScope());
        context.setVariable("testCases", document.getTestCases());

        // Statistics variables
        if (templateConfig != null && templateConfig.isIncludeStatistics()) {
            context.setVariable("statistics", statistics);
            context.setVariable("includeStatistics", true);
        } else {
            context.setVariable("includeStatistics", false);
        }

        return context;
    }

}
