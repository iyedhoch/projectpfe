package com.pfe.docgen.statistics;


import com.pfe.docgen.document.DocumentStatistics;
import com.pfe.docgen.document.DocumentTestPlan;

public interface StatisticsService {

    DocumentStatistics computeStatistics(DocumentTestPlan document);
}
