package com.pfe.docgen.statistics;
import com.pfe.docgen.document.*;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Override
    public DocumentStatistics computeStatistics(DocumentTestPlan document) {

        int total = 0;
        int passed = 0;
        int failed = 0;
        int blocked = 0;

        for (DocumentTestCase testCase : document.getTestCases()) {
            for (DocumentTestExecution exec : testCase.getExecutions()) {
                total++;

                switch (exec.getStatus()) {
                    case "PASSED" -> passed++;
                    case "FAILED" -> failed++;
                    case "BLOCKED" -> blocked++;
                }
            }
        }

        double passRate = total == 0 ? 0 : ((double) passed / total) * 100;

        return DocumentStatistics.builder()
                .totalTests(total)
                .passed(passed)
                .failed(failed)
                .blocked(blocked)
                .passRate(passRate)
                .build();
    }
}