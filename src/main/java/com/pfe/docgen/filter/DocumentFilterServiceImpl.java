package com.pfe.docgen.filter;

import com.pfe.docgen.document.DocumentTestCase;
import com.pfe.docgen.document.DocumentTestExecution;
import com.pfe.docgen.document.DocumentTestPlan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentFilterServiceImpl implements DocumentFilterService {

    @Override
    public DocumentTestPlan applyFilters(DocumentTestPlan document,
                                         DocumentFilter filter) {

        if (filter == null) {
            return document;
        }

        List<DocumentTestCase> filteredCases = document.getTestCases()
                .stream()
                .map(testCase -> {

                    // Filter executions by status
                    if (filter.getExecutionStatus() != null) {

                        List<DocumentTestExecution> filteredExecutions =
                                testCase.getExecutions()
                                        .stream()
                                        .filter(exec ->
                                                filter.getExecutionStatus()
                                                        .equalsIgnoreCase(exec.getStatus()))
                                        .collect(Collectors.toList());

                        testCase.setExecutions(filteredExecutions);
                    }

                    // Remove executions entirely if disabled
                    if (!filter.isIncludeExecutions()) {
                        testCase.setExecutions(List.of());
                    }

                    // Remove expected result if disabled
                    if (!filter.isIncludeExpectedResults()) {
                        testCase.setExpectedResult(null);
                    }

                    return testCase;
                })
                .collect(Collectors.toList());

        document.setTestCases(filteredCases);

        return document;
    }
}
