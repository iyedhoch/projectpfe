package com.pfe.docgen.export;


import com.pfe.docgen.document.DocumentTestPlan;

public interface ExcelExportService {
    byte[] generateExcel(DocumentTestPlan document);
}
