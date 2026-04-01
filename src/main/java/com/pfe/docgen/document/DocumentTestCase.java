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
public class DocumentTestCase {
    private String name;
    private String description;
    private String expectedResult;
    private List<DocumentTestExecution> executions;
}
