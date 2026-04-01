package com.pfe.docgen.controller;

import com.pfe.docgen.dto.TestPlanDTO;
import com.pfe.docgen.filter.DocumentFilter;
import com.pfe.docgen.service.DocumentGenerationService;
import com.pfe.docgen.service.TestPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testplans")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Test Plans", description = "Test plan management and document generation endpoints")
public class TestPlanController {

    private final TestPlanService testPlanService;
    private final DocumentGenerationService documentGenerationService;

    @GetMapping
    @Operation(summary = "Get all test plans", description = "Retrieve a list of all available test plans")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Test plans retrieved successfully",
            content = @Content(schema = @Schema(implementation = TestPlanDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public List<TestPlanDTO> getAllTestPlans() {
        return testPlanService.getAllTestPlans();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get test plan by ID", description = "Retrieve a specific test plan by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Test plan retrieved successfully",
            content = @Content(schema = @Schema(implementation = TestPlanDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Test plan not found")
    })
    public TestPlanDTO getTestPlanById(@PathVariable Long id) {
        return testPlanService.getTestPlanById(id);
    }

    @GetMapping("/{id}/document/html")
    @Operation(summary = "Generate HTML document", description = "Generate an HTML document version of the test plan")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "HTML document generated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Test plan not found")
    })
    public ResponseEntity<String> generateHtml(
            @PathVariable Long id,
            @RequestParam(required = false) String template
    ) {

        String html = documentGenerationService.generateHtmlDocument(id, template);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }


    @GetMapping("/{id}/document/pdf")
    @Operation(summary = "Generate PDF document", description = "Generate a PDF document version of the test plan")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PDF document generated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Test plan not found")
    })
    public ResponseEntity<byte[]> generatePdf(
            @PathVariable Long id,
            @RequestParam(required = false) String template
    ) {

        byte[] pdf = documentGenerationService
                .generatePdfDocument(id, template);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=testplan-" + id + ".pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


    @GetMapping("/{id}/document/word")
    @Operation(summary = "Generate Word document", description = "Generate a Word (.docx) document version of the test plan")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Word document generated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Test plan not found")
    })
    public ResponseEntity<byte[]> generateWord(
            @PathVariable Long id,
            @RequestParam(required = false) String template
    ) {

        byte[] word = documentGenerationService.generateWordDocument(id, template);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=testplan-" + id + ".docx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                        )
                )
                .body(word);
    }


    @GetMapping("/{id}/document/excel")
    @Operation(summary = "Generate Excel document", description = "Generate an Excel (.xlsx) document version of the test plan")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Excel document generated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Test plan not found")
    })
    public ResponseEntity<byte[]> generateExcel(
            @PathVariable Long id,
            @RequestParam(required = false) String template

    ) {

        byte[] excel = documentGenerationService.generateExcelDocument(id, template);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=testplan-" + id + ".xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(excel);
    }

}
