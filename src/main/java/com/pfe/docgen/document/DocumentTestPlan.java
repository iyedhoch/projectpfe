package com.pfe.docgen.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentTestPlan {
    private String title;
    private String build;
    private String scope;
    private List<DocumentTestCase> testCases;
}
