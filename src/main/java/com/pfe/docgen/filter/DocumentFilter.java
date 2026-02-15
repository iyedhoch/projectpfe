package com.pfe.docgen.filter;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentFilter {

    private String executionStatus;
    private boolean includeExecutions;
    private boolean includeExpectedResults;
    private boolean includeStatistics;
}
