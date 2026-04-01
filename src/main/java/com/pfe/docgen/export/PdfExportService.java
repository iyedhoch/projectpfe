package com.pfe.docgen.export;


public interface PdfExportService {
    byte[] generatePdfFromHtml(String html);
}

