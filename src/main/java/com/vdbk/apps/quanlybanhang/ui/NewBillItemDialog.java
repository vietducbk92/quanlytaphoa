/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.ui;

import com.vdbk.apps.quanlybanhang.barcode.utils.Utils;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author vietd
 */
public class NewBillItemDialog extends JDialog implements ActionListener {

    private static String TITLE = "HÀNG CHƯA CÓ TRONG KHO";
    private BillItem billItem;
    private BillItem outBillItem;
    private String barcode;
    private boolean hasBarcode = false;
    private JTextField edtName;//ten
    private JTextField edtPrice;
    private JTextField edtUnit;
    private JTextField edtNumber;
    private JTextField edtTotalAmount;
    private JButton btnOk;
    private JButton btnCancel;

    public NewBillItemDialog(JFrame parent, String barcode) {
        super(parent, TITLE, true);
        hasBarcode = barcode != null;
        //barcode 
        if (!hasBarcode) {
            this.barcode = System.currentTimeMillis() + "";
        } else {
            this.barcode = barcode;
        }
        setPreferredSize(new Dimension(500, 500));

        addComponentToDialog();
        Point loc = parent.getLocation();
        setLocation(loc.x + parent.getWidth() / 2 - getWidth() / 2, loc.y + parent.getHeight() / 2 - getHeight() / 2);

        //listener number & total amount updating
        edtNumber.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                if (!edtNumber.isFocusOwner()) {
                    return;
                }

                if (edtNumber.getText().isEmpty()) {
                    return;
                }
                float number = Float.parseFloat(edtNumber.getText());
                billItem.updateNumber(number);
                Locale localeVN = new Locale("vi", "VN");
                NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
                edtTotalAmount.setText(Utils.fmt(billItem.getTotalAmount()));
            }
        });

        edtTotalAmount.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                if (!edtTotalAmount.isFocusOwner()) {
                    return;
                }
                if (edtTotalAmount.getText().isEmpty()) {
                    return;
                }

                double amount = Double.parseDouble(edtTotalAmount.getText());
                billItem.updateTotalAmount(amount);
                //edtNumber.setText(billItem.getNumber() + "");
                edtNumber.setText(Utils.fmt(billItem.getNumber()));
            }
        });

    }

    private void addComponentToDialog() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        //Name
        JLabel label = new JLabel("TÊN HÀNG");
        label.setFont(Constants.FONT_CONTENT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label, gbc);

        edtName = new JTextField();
        edtName.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(edtName, gbc);

        //price
        label = new JLabel("ĐƠN GIÁ");
        label.setFont(Constants.FONT_CONTENT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(label, gbc);

        edtPrice = new JTextField();
        edtPrice.setFont(Constants.FONT_TITLE);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(edtPrice, gbc);
        
        label = new JLabel("VND");
        label.setFont(Constants.FONT_CONTENT);
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(label, gbc);

        //don vi
        label = new JLabel("ĐƠN VỊ");
        label.setFont(Constants.FONT_CONTENT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(label, gbc);

        edtUnit = new JTextField("cái");
        edtUnit.setFont(Constants.FONT_TITLE);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(edtUnit, gbc);

        //number
        label = new JLabel("SỐ LƯỢNG:");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(label, gbc);
        edtNumber = new JTextField();
        edtNumber.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(edtNumber, gbc);
        label = new JLabel(billItem.getItem().getUnit());
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        //thanh tien
        label = new JLabel("THÀNH TIỀN:");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(label, gbc);
        edtTotalAmount = new JTextField("", 9);
        edtTotalAmount.setFont(Constants.FONT_CONTENT);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(edtTotalAmount, gbc);
        label = new JLabel("VND");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        //fill item data to UI
        barcode = billItem.getItem().getId();

        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        edtNumber.setText(Utils.fmt(billItem.getNumber()));
        edtTotalAmount.setText(Utils.fmt(billItem.getTotalAmount()));;

        //button
        JLabel spacer = new JLabel(" ");
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(spacer, gbc);

        btnOk = new JButton("THÊM");
        btnOk.setFont(Constants.FONT_CONTENT);
        btnOk.addActionListener(this);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(btnOk, gbc);
        btnCancel = new JButton("HỦY");
        btnCancel.setFont(Constants.FONT_CONTENT);
        btnCancel.addActionListener(this);
        gbc.gridwidth = 1;
        gbc.gridx = 1;
        gbc.gridy = 6;
        panel.add(btnCancel, gbc);

        getContentPane().add(panel);
        pack();
    }

    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if (source == btnOk) {
            outBillItem = billItem;
        } else {
            outBillItem = null;
        }
        dispose();
    }

    public BillItem run() {
        this.setVisible(true);
        return outBillItem;
    }
}
