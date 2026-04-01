package com.pfe.docgen.export;

import com.pfe.docgen.document.DocumentTestPlan;
import com.pfe.docgen.document.DocumentTestCase;
import com.pfe.docgen.document.DocumentTestExecution;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class WordExportServiceImpl implements WordExportService {

    @Override
    public byte[] generateWord(DocumentTestPlan document) {

        try (XWPFDocument wordDoc = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XWPFParagraph title = wordDoc.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText(document.getTitle());
            titleRun.setBold(true);
            titleRun.setFontSize(18);

            XWPFParagraph meta = wordDoc.createParagraph();
            XWPFRun metaRun = meta.createRun();
            metaRun.setText("Build: " + document.getBuild());
            metaRun.addBreak();
            metaRun.setText("Scope: " + document.getScope());

            for (DocumentTestCase testCase : document.getTestCases()) {

                XWPFParagraph tcTitle = wordDoc.createParagraph();
                XWPFRun tcRun = tcTitle.createRun();
                tcRun.setBold(true);
                tcRun.setText(testCase.getName());

                XWPFParagraph desc = wordDoc.createParagraph();
                desc.createRun().setText(testCase.getDescription());

                XWPFParagraph expected = wordDoc.createParagraph();
                expected.createRun().setText("Expected: " + testCase.getExpectedResult());

                for (DocumentTestExecution exec : testCase.getExecutions()) {
                    XWPFParagraph execP = wordDoc.createParagraph();
                    execP.createRun().setText(
                            exec.getStatus() + " - " + exec.getComment()
                    );
                }
            }

            wordDoc.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating Word document", e);
        }
    }
}
