/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.ui;

import java.awt.Color;
import java.awt.TextArea;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author vietd
 */
public class JMultilineLabel extends JTextArea {

    private static final long serialVersionUID = 1L;

    public JMultilineLabel(String text) {
        super(text);
        setEditable(false);
        setCursor(null);
        setOpaque(false);
        setFocusable(false);
        setFont(UIManager.getFont("Label.font"));
        setWrapStyleWord(true);
        setLineWrap(true);
        //According to Mariana this might improve it
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setAlignmentY(JLabel.CENTER_ALIGNMENT);
        setBackground(new Color(0,0,0,0));
    }

    public JMultilineLabel() {
        super();
        setEditable(false);
        setCursor(null);
        setOpaque(false);
        setFocusable(false);
        setWrapStyleWord(true);
        setLineWrap(true);
        //According to Mariana this might improve it
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setAlignmentY(JLabel.CENTER_ALIGNMENT);
        setBackground(new Color(0,0,0,0));
    }

    @Override
    public void setText(String t) {
        super.setText(t); //To change body of generated methods, choose Tools | Templates.
    }
    
}
