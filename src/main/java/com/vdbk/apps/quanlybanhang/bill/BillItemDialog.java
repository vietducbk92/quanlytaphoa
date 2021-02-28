/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.bill;

import com.vdbk.apps.quanlybanhang.bill.NewBillItem;
import com.vdbk.apps.quanlybanhang.barcode.utils.Utils;
import com.vdbk.apps.quanlybanhang.ui.Constants;
import com.vdbk.apps.quanlybanhang.ui.FocusTraversal;
import com.vdbk.apps.quanlybanhang.ui.JMultilineLabel;
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
//show khi thêm hàng không có mã vạch vào kho
//hoặc là show khi double click vào hàng trong hóa đơn để sửa số lượng/giá bán
public class BillItemDialog extends JDialog implements ActionListener {

    private static String TITLE = "SẢN PHẨM";
    private NewBillItem billItem;
    private BillItem outBillItem;
    private String barcode;
    private JLabel tvName;//ten
    private JLabel tvPrice;
    private JMultilineLabel tvNote;
    private JTextField edtNumber;
    private JTextField edtTotalPrice;
    private JButton btnOk;
    private JButton btnCancel;

    public BillItemDialog(JFrame parent, NewBillItem item) {
        super(parent, TITLE, true);
        if (item == null) {
            return;
        }
        setPreferredSize(new Dimension(500, 500));
        this.billItem = item;
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
                tvPrice.setText("ĐƠN GIÁ: " + currencyVN.format(item.getUnitPrice()) + "/" + billItem.getUnit());
                edtTotalPrice.setText(Utils.fmt(billItem.getTotalPrice()));
            }
        });

        edtTotalPrice.getDocument().addDocumentListener(new DocumentListener() {
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
                if (!edtTotalPrice.isFocusOwner()) {
                    return;
                }
                if (edtTotalPrice.getText().isEmpty()) {
                    return;
                }
               
                double totalPrice = Double.parseDouble(edtTotalPrice.getText());
                billItem.updateTotalPrice(totalPrice);
                Locale localeVN = new Locale("vi", "VN");
                NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
                tvPrice.setText("ĐƠN GIÁ: " + currencyVN.format(item.getUnitPrice()) + "/" + billItem.getUnit());
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
        tvName = new JLabel();
        tvName.setFont(Constants.FONT_HEADER);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        panel.add(tvName, gbc);

        //price
        tvPrice = new JLabel();
        tvPrice.setFont(Constants.FONT_TITLE);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        panel.add(tvPrice, gbc);
        //note
        tvNote = new JMultilineLabel();
        tvNote.setFont(Constants.FONT_CONTENT);
        tvNote.setMaximumSize(new Dimension(500, 60));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        panel.add(tvNote, gbc);

        //number
        JLabel label = new JLabel("SỐ LƯỢNG:");
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
        label = new JLabel(billItem.getUnit());
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
        edtTotalPrice = new JTextField("", 9);
        edtTotalPrice.setFont(Constants.FONT_CONTENT);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(edtTotalPrice, gbc);
        label = new JLabel("VND");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        //fill item data to UI
        barcode = billItem.getId();
        tvName.setText(billItem.getName());

        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        tvPrice.setText("ĐƠN GIÁ: " + currencyVN.format(billItem.getUnitPrice()) + "/" + billItem.getUnit());
        tvNote.setText(billItem.getNote());
        
        edtNumber.setText(Utils.fmt(billItem.getNumber()));
        edtTotalPrice.setText(Utils.fmt(billItem.getTotalPrice()));;

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
        
        
        FocusTraversal traversal = new FocusTraversal(edtNumber,edtTotalPrice);
        traversal.setListener(() -> {
            outBillItem = billItem;
            dispose();
        });

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
