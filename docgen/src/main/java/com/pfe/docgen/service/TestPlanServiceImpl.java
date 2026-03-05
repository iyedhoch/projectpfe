package com.pfe.docgen.service;

import com.pfe.docgen.DAO.TestPlanDao;
import com.pfe.docgen.dto.TestCaseDTO;
import com.pfe.docgen.dto.TestExecutionDTO;
import com.pfe.docgen.dto.TestPlanDTO;
import com.pfe.docgen.entity.TestPlan;
import com.pfe.docgen.repository.TestPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestPlanServiceImpl implements TestPlanService {

    private final TestPlanDao testPlanDao;

    @Override
    public List<TestPlanDTO> getAllTestPlans() {
        return testPlanDao.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public TestPlanDTO getTestPlanById(Long id) {
        TestPlan testPlan = testPlanDao.findById(id)
                .orElseThrow(() -> new RuntimeException("TestPlan not found"));

        return mapToDTO(testPlan);
    }

    private TestPlanDTO mapToDTO(TestPlan testPlan) {

        return TestPlanDTO.builder()
                .id(testPlan.getId())
                .name(testPlan.getName())
                .build(testPlan.getBuild())
                .scope(testPlan.getScope())
                .testCases(
                        testPlan.getTestCases().stream().map(testCase ->
                                TestCaseDTO.builder()
                                        .id(testCase.getId())
                                        .name(testCase.getName())
                                        .description(testCase.getDescription())
                                        .expectedResult(testCase.getExpectedresult())
                                        .testExecutions(
                                                testCase.getTestexecutions().stream().map(execution ->
                                                        TestExecutionDTO.builder()
                                                                .id(execution.getId())
                                                                .status(execution.getStatus().name())
                                                                .comment(execution.getComment())
                                                                .date(execution.getDate())
                                                                .build()
                                                ).toList()
                                        )
                                        .build()
                        ).toList()
                )
                .build();
    }
}
