package com.github.zzbbc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfBoxUtils {
    static Logger LOGGER = LoggerFactory.getLogger(PdfBoxUtils.class);

    private static final String ARIAL_FONT_PATH = "arial.ttf";
    private static final String ARIAL_BOLD_FONT_PATH = "arialbd.ttf";
    private static final String ARIAL_ITALIC_FONT_PATH = "ariali.ttf";

    public static void addParamsToForm(PDDocument pdfDocument, String fontFolderPath,
            Map<String, String> params) throws FileNotFoundException, IOException {
        // get the document catalog
        PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();

        PDAcroForm acroForm = docCatalog.getAcroForm();
        final PDResources resources = new PDResources();

        // Add aditional font
        PDFont arial = PDType0Font.load(pdfDocument,
                new FileInputStream(fontFolderPath + File.separator + ARIAL_FONT_PATH), false);
        PDFont arialBold = PDType0Font.load(pdfDocument,
                new FileInputStream(fontFolderPath + File.separator + ARIAL_BOLD_FONT_PATH), false);
        PDFont arialItalic = PDType0Font.load(pdfDocument,
                new FileInputStream(fontFolderPath + File.separator + ARIAL_ITALIC_FONT_PATH),
                false);

        resources.put(COSName.getPDFName("Arial"), arial);
        resources.put(COSName.getPDFName("Arial,Bold"), arialBold);
        resources.put(COSName.getPDFName("Arial,Italic"), arialItalic);

        acroForm.setDefaultResources(resources);
        for (PDField field : acroForm.getFields()) {
            String key = field.getFullyQualifiedName();
            if (params.containsKey(key)) {
                LOGGER.debug("Add params: ");
                LOGGER.debug("Key: {}", key);
                switch (field.getFieldType()) {
                    case "Tx":
                        PDTextField textField = (PDTextField) field;
                        textField.setValue(params.get(key));

                        LOGGER.debug("Appearance: {}, Value: {}", textField.getDefaultAppearance(),
                                params.get(key));
                        break;
                    case "Btn":
                        break;
                }
            }

            field.setReadOnly(true);
        }
    }
}
