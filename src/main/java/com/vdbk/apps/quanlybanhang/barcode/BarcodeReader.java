/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.barcode;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 *
 * @author DucNV23
 */
public class BarcodeReader {

private static final long THRESHOLD = 100;
private static final int MIN_BARCODE_LENGTH = 8;

public interface BarcodeListener {

    void onBarcodeRead(String barcode);
}

private final StringBuffer barcode = new StringBuffer();
private final ArrayList<BarcodeListener> listeners = new ArrayList<BarcodeListener>();
private long lastEventTimeStamp = 0L;

private static BarcodeReader instance = null;

public synchronized static BarcodeReader getInstance(){
    if(instance == null)
        instance = new BarcodeReader();
    return instance;
}

public BarcodeReader() {

    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() != KeyEvent.KEY_RELEASED) {
                return false;
            }
           
            if (e.getWhen() - lastEventTimeStamp > THRESHOLD) {
                barcode.delete(0, barcode.length());
            }

            lastEventTimeStamp = e.getWhen();

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (barcode.length() >= MIN_BARCODE_LENGTH) {
                    fireBarcode(barcode.toString());
                }
                barcode.delete(0, barcode.length());
            } else {
                barcode.append(e.getKeyChar());
            }
            return false;
        }
    });

}

protected void fireBarcode(String barcode) {
    for (BarcodeListener listener : listeners) {
        listener.onBarcodeRead(barcode);
    }
}

public void addBarcodeListener(BarcodeListener listener) {
    listeners.add(listener);
}

public void removeBarcodeListener(BarcodeListener listener) {
    listeners.remove(listener);
}

public void clearAllBarcodeListener(){
    listeners.clear();
}

}