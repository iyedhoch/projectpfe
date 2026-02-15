package com.pfe.docgen.mapper;

import com.pfe.docgen.document.DocumentTestCase;
import com.pfe.docgen.document.DocumentTestExecution;
import com.pfe.docgen.document.DocumentTestPlan;
import com.pfe.docgen.dto.TestPlanDTO;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    public DocumentTestPlan toDocumentModel(TestPlanDTO dto) {

        return DocumentTestPlan.builder()
                .title(dto.getName())
                .build(dto.getBuild())
                .scope(dto.getScope())
                .testCases(
                        dto.getTestCases().stream().map(testCase ->
                                DocumentTestCase.builder()
                                        .name(testCase.getName())
                                        .description(testCase.getDescription())
                                        .expectedResult(testCase.getExpectedResult())
                                        .executions(
                                                testCase.getTestExecutions().stream().map(execution ->
                                                        DocumentTestExecution.builder()
                                                                .status(execution.getStatus())
                                                                .comment(execution.getComment())
                                                                .executionDate(
                                                                        execution.getDate().toString()
                                                                )
                                                                .build()
                                                ).toList()
                                        )
                                        .build()
                        ).toList()
                )
                .build();
    }
}

