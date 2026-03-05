package com.pfe.docgen.filter;

import com.pfe.docgen.document.DocumentTestPlan;

public interface DocumentFilterService {

    DocumentTestPlan applyFilters(DocumentTestPlan document,
                                  DocumentFilter filter);

    DocumentTestPlan filterOnlyFailed(DocumentTestPlan document);
}
