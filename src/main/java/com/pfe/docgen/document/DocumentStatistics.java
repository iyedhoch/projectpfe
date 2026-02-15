package com.pfe.docgen.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentStatistics {

    private int totalTests;
    private int passed;
    private int failed;
    private int blocked;
    private double passRate;
}
