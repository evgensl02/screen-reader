package org.evgensl.sreenreader.orc;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;

public class OcrService {

    private final ITesseract tesseract;

    public OcrService() {
        tesseract = new Tesseract();
        tesseract.setLanguage("eng+rus");
        tesseract.setDatapath("D:\\evgensl\\Tesseract-OCR\\tessdata");
    }

    public String recognize(BufferedImage image) {
        try {
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
    }
}
