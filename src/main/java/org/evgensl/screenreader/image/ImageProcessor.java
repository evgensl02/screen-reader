package org.evgensl.screenreader.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageProcessor {

    public BufferedImage upscale(BufferedImage image, int scale) {
        int width = image.getWidth() * scale;
        int height = image.getHeight() * scale;
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = result.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return result;
    }
}
