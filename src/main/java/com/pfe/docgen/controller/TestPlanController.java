package com.pfe.docgen.controller;

import com.pfe.docgen.dto.TestPlanDTO;
import com.pfe.docgen.filter.DocumentFilter;
import com.pfe.docgen.service.DocumentGenerationService;
import com.pfe.docgen.service.TestPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testplans")
@RequiredArgsConstructor
public class TestPlanController {

    private final TestPlanService testPlanService;
    private final DocumentGenerationService documentGenerationService;

    @GetMapping
    public List<TestPlanDTO> getAllTestPlans() {
        return testPlanService.getAllTestPlans();
    }

    @GetMapping("/{id}")
    public TestPlanDTO getTestPlanById(@PathVariable Long id) {
        return testPlanService.getTestPlanById(id);
    }

    @GetMapping("/{id}/document/html")
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
