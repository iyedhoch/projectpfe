package com.pfe.docgen.service;

import com.pfe.docgen.dto.TestPlanDTO;

import java.util.List;

public interface TestPlanService {
    List<TestPlanDTO> getAllTestPlans();

    TestPlanDTO getTestPlanById(Long id);
}
