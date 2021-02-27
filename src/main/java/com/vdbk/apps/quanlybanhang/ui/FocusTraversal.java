/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JTextField;

/**
 *
 * @author vietd
 */
public class FocusTraversal {

    public interface FocusTraversalListener {

        void onFinish();
    }
    private ArrayList<Component> order;
    private FocusTraversalListener listener;

    public FocusTraversal(Component... order) {
        this.order = new ArrayList<>(Arrays.asList(order));
        if (order.length > 0) {
            getDefaultComponent().requestFocusInWindow();
            for (Component component : order) {
                if (component instanceof JTextField) {
                    ((JTextField) component).addActionListener(actionListener);
                }
            }
        }
    }

    public void setListener(FocusTraversalListener listener) {
        this.listener = listener;
    }

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Component source = (Component) e.getSource();
            if (source instanceof JTextField) {
                if (isLastComponent(source)) {
                    if (listener != null) {
                        listener.onFinish();
                    }
                } else {
                    Component after = getComponentAfter(source);
                    after.requestFocus();
                }
            }
        }
    };

    private Component getComponentAfter(Component aComponent) {
        int idx = (order.indexOf(aComponent) + 1) % order.size();
        return order.get(idx);
    }

    private Component getComponentBefore(Component aComponent) {
        int idx = order.indexOf(aComponent) - 1;
        if (idx < 0) {
            idx = order.size() - 1;
        }
        return order.get(idx);
    }

    private Component getDefaultComponent() {
        return order.get(0);
    }

    private Component getLastComponent() {
        return order.size() > 0 ? order.get(order.size()) : null;
    }

    private boolean isLastComponent(Component aComponent) {
        if (order.indexOf(aComponent) == (order.size() - 1)) {
            return true;
        } else {
            return false;
        }
    }

    private Component getFirstComponent() {
        return order.get(0);
    }
}
