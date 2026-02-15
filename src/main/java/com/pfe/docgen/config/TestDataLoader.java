package com.pfe.docgen.config;

import com.pfe.docgen.entity.*;
import com.pfe.docgen.repository.TestPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class TestDataLoader {

    private final TestPlanRepository testPlanRepository;

    @Bean
    CommandLineRunner loadData() {
        return args -> {

            TestExecution execution = TestExecution.builder()
                    .status(ExecutionStatus.PASSED)
                    .comment("Test passed successfully")
                    .date(LocalDateTime.now())
                    .build();

            TestCase testCase = TestCase.builder()
                    .name("Login Test")
                    .description("Verify user login")
                    .expectedresult("User logged in")
                    .testexecutions(List.of(execution))
                    .build();

            execution.setTestcase(testCase);

            TestPlan testPlan = TestPlan.builder()
                    .name("Authentication Tests")
                    .build("v1.0")
                    .scope("Login module")
                    .testCases(List.of(testCase))
                    .build();

            testCase.setTestPlan(testPlan);

            testPlanRepository.save(testPlan);
        };
    }
}
