package com.pfe.docgen.export;

import com.pfe.docgen.document.DocumentTestPlan;

public interface WordExportService {
    byte[] generateWord(DocumentTestPlan document);
}
