package com.pfe.docgen.service;

import com.pfe.docgen.filter.DocumentFilter;
import com.pfe.docgen.statistics.StatisticsService;

public interface DocumentGenerationService {


    String generateHtmlDocument(Long testPlanId, String templateName);

    byte[] generatePdfDocument(Long testPlanId, String templateName);

    byte[] generateWordDocument(Long testPlanId, String templateName);

    byte[] generateExcelDocument(Long testPlanId, String templateName);

}
