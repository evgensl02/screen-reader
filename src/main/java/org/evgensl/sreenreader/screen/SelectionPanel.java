package org.evgensl.sreenreader.screen;

import javax.swing.*;
import java.awt.*;

public class SelectionPanel extends JPanel {

    private Rectangle selection;

    public SelectionPanel() {
        setOpaque(false);
        setFocusable(true);
    }

    public void setSelection(Rectangle selection) {
        this.selection = selection;
        repaint();
    }

    public void clearSelection() {
        this.selection = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 255));
        g2.fillRect(
                0,
                0,
                getWidth(),
                getHeight()
        );
        if (selection != null) {
            g2.setComposite(
                    AlphaComposite.Clear
            );

            g2.fillRect(
                    selection.x,
                    selection.y,
                    selection.width,
                    selection.height
            );

            g2.setComposite(
                    AlphaComposite.SrcOver
            );


            g2.setColor(Color.WHITE);

            g2.setStroke(
                    new BasicStroke(2)
            );

            g2.drawRect(
                    selection.x,
                    selection.y,
                    selection.width,
                    selection.height
            );
        }
        g2.dispose();
    }
}
