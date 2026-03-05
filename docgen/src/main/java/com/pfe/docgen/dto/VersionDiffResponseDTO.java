package com.pfe.docgen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionDiffResponseDTO {

    private Map<String, Object> addedFields;

    private Map<String, Object> removedFields;

    private Map<String, Map<String, Object>> changedFields;
}