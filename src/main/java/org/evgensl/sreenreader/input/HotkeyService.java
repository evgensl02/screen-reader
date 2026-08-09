package org.evgensl.sreenreader.input;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class HotkeyService {

    private final Runnable selectAction;
    private final Runnable cancelAction;
    private final AtomicBoolean selectTriggered = new AtomicBoolean(false);

    public HotkeyService(Runnable selectAction, Runnable cancelAction) {
        this.selectAction = selectAction;
        this.cancelAction = cancelAction;
    }

    public void start() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }

        GlobalScreen.addNativeKeyListener(
                new NativeKeyListener() {
                    @Override
                    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                        boolean ctrl = (nativeEvent.getModifiers() & NativeInputEvent.CTRL_MASK) != 0;
                        boolean shift = (nativeEvent.getModifiers() & NativeInputEvent.SHIFT_MASK) != 0;

                        if (ctrl && shift) {
                            if (selectTriggered.compareAndSet(false, true)) {
                                SwingUtilities.invokeLater(selectAction);
                            }
                        }

                        if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_F7) {
                            SwingUtilities.invokeLater(cancelAction);
                        }
                    }

                    @Override
                    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                        boolean ctrl = (nativeEvent.getModifiers() & NativeInputEvent.CTRL_MASK) != 0;
                        boolean shift = (nativeEvent.getModifiers() & NativeInputEvent.SHIFT_MASK) != 0;

                        if (!ctrl || !shift) {
                            selectTriggered.set(false);
                        }
                    }
                }
        );
    }
}
