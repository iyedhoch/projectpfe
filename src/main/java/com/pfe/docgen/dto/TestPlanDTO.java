package com.pfe.docgen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestPlanDTO {

    private Long id;
    private String name;
    private String build;
    private String scope;
    private List<TestCaseDTO> testCases;
}