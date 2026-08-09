package org.evgensl.screenreader.screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.CompletableFuture;

public class ScreenSelector {

    private final JWindow window;
    private final SelectionPanel selectionPanel;
    private CompletableFuture<Rectangle> future;
    private Point startPoint;
    private Point currentPoint;
    private boolean isSelecting;

    public ScreenSelector() {
        window = new JWindow();
        selectionPanel = new SelectionPanel();

        createWindow();
        registerListeners();
    }

    public CompletableFuture<Rectangle> select() {
        if (isSelecting) {
            return future;
        }
        future = new CompletableFuture<>();
        resetSelection();
        isSelecting = true;
        window.setVisible(true);
        window.toFront();
        window.requestFocus();
        selectionPanel.requestFocus();
        return future;
    }

    private void createWindow() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setBounds(0, 0, screenSize.width, screenSize.height);
        window.setAlwaysOnTop(true);
        window.setBackground(new Color(0, 0, 0, 0));
        window.setOpacity(0.5f);
        window.setContentPane(selectionPanel);
        window.setFocusable(true);
    }

    private void registerListeners() {
        SelectionMouseAdapter mouseAdapter = new SelectionMouseAdapter();
        selectionPanel.addMouseListener(mouseAdapter);
        selectionPanel.addMouseMotionListener(mouseAdapter);

    }

    private void updateSelection(Point point) {
        currentPoint = point;
        Rectangle rectangle = calculateRectangle();
        selectionPanel.setSelection(rectangle);
    }

    private void finishSelection(Point point) {
        currentPoint = point;
        Rectangle rectangle = calculateRectangle();
        if (rectangle.width <=5 || rectangle.height <= 5) {
            cancelSelection();
            return;
        }
        isSelecting = false;
        window.setVisible(false);
        if (!future.isDone()) {
            future.complete(rectangle);
        }
    }

    private Rectangle calculateRectangle() {
        int x = Math.min(startPoint.x, currentPoint.x);
        int y = Math.min(startPoint.y, currentPoint.y);
        int width = Math.abs(startPoint.x - currentPoint.x);
        int height = Math.abs(startPoint.y - currentPoint.y);
        return new Rectangle(x, y, width, height);
    }

    private void resetSelection() {
        startPoint = null;
        currentPoint = null;
        selectionPanel.clearSelection();
    }

    public void cancelSelection() {
        if (!isSelecting){
            return;
        }
        isSelecting = false;
        window.setVisible(false);
        resetSelection();
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
    }

    private class SelectionMouseAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (!isSelecting){
                return;
            }
            startPoint = e.getPoint();
            currentPoint = startPoint;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!isSelecting){
                return;
            }
            updateSelection(e.getPoint());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!isSelecting){
                return;
            }
            finishSelection(e.getPoint());
        }
    }
}
