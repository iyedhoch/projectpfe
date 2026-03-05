package com.pfe.docgen.export;

import com.pfe.docgen.document.DocumentTestPlan;
import com.pfe.docgen.document.DocumentTestCase;
import com.pfe.docgen.document.DocumentTestExecution;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Override
    public byte[] generateExcel(DocumentTestPlan document) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Test Results");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Test Case");
            header.createCell(1).setCellValue("Description");
            header.createCell(2).setCellValue("Status");
            header.createCell(3).setCellValue("Comment");

            int rowIdx = 1;

            for (DocumentTestCase testCase : document.getTestCases()) {
                for (DocumentTestExecution exec : testCase.getExecutions()) {

                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(testCase.getName());
                    row.createCell(1).setCellValue(testCase.getDescription());
                    row.createCell(2).setCellValue(exec.getStatus());
                    row.createCell(3).setCellValue(exec.getComment());
                }
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel file", e);
        }
    }
}
