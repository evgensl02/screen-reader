package org.evgensl.screenreader.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenshotService {

    private final Robot robot;

    public ScreenshotService() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage capture(Rectangle rectangle) {
        return robot.createScreenCapture(rectangle);
    }
}
