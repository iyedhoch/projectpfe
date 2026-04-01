package com.pfe.docgen.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentTestExecution {

    private String status;
    private String comment;
    private String executionDate;
}

