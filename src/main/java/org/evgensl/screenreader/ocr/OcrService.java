package org.evgensl.screenreader.ocr;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

import java.awt.image.BufferedImage;
import java.io.File;

public class OcrService {

    private final ITesseract tesseract;

    public OcrService() {
        this(resolveDatapath());
    }

    public OcrService(String datapath) {
        tesseract = new Tesseract();
        tesseract.setLanguage("eng+rus");
        if (datapath != null && !datapath.isBlank()) {
            tesseract.setDatapath(datapath);
        }
    }

    public String recognize(BufferedImage image) {
        try {
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
    }

    private static String resolveDatapath() {
        String prop = System.getProperty("tessdata.path");
        if (prop != null && !prop.isBlank()) {
            return prop;
        }

        File bundled = LoadLibs.extractTessResources("tessdata");
        if (bundled != null && bundled.exists()) {
            return bundled.getAbsolutePath();
        }

        String env = System.getenv("TESSDATA_PREFIX");
        if (env != null && !env.isBlank()) {
            return env;
        }

        File localTessdata = new File("./tessdata");
        if (localTessdata.exists() && localTessdata.isDirectory()) {
            return localTessdata.getAbsolutePath();
        }

        String userHome = System.getProperty("user.home");
        if (userHome != null) {
            File userTessdata = new File(userHome, "AppData/Local/Programs/Tesseract-OCR/tessdata");
            if (userTessdata.exists() && userTessdata.isDirectory()) {
                return userTessdata.getAbsolutePath();
            }
        }

        File progFilesTessdata = new File("C:/Program Files/Tesseract-OCR/tessdata");
        if (progFilesTessdata.exists() && progFilesTessdata.isDirectory()) {
            return progFilesTessdata.getAbsolutePath();
        }

        return "tessdata";
    }
}
