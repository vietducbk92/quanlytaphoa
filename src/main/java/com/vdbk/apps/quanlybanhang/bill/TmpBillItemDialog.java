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
public class TmpBillItemDialog extends JDialog implements ActionListener {
    
    private static String TITLE = "SẢN PHẨM MỚI";
    private BillItem billItem = null;
    private JTextField edtName;
    private JTextField edtTotalPrice;
    private JButton btnOk;
    private JButton btnCancel;
    private String name;
    private double totalPrice;
    
    public TmpBillItemDialog(JFrame parent,String name,double totalPrice) {
        super(parent, TITLE, true);
        setPreferredSize(new Dimension(350, 250));
        this.name = name;
        this.totalPrice = totalPrice;
        addComponentToDialog();
        Point loc = parent.getLocation();
        setLocation(loc.x + parent.getWidth() / 2 - getWidth() / 2, loc.y + parent.getHeight() / 2 - getHeight() / 2);
    }
    
    private void addComponentToDialog() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        //Name
        JLabel label = new JLabel("TÊN HÀNG");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label, gbc);

        //Price
        label = new JLabel("GIÁ");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        
        panel.add(label, gbc);
        
        edtName = new JTextField(name);
        edtName.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(edtName, gbc);
        
        edtTotalPrice = new JTextField(30);
        edtTotalPrice.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        if(totalPrice > 0){
            edtTotalPrice.setText(Utils.fmt(totalPrice));
        }
        panel.add(edtTotalPrice, gbc);
        
        btnOk = new JButton("THÊM");
        btnOk.setFont(Constants.FONT_CONTENT);
        btnOk.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        panel.add(btnOk, gbc);
        btnCancel = new JButton("HỦY");
        btnCancel.setFont(Constants.FONT_CONTENT);
        btnCancel.addActionListener(this);
        gbc.gridx = 2;
        gbc.gridy = 3;
        panel.add(btnCancel, gbc);
        
        FocusTraversal focusTraversal = new FocusTraversal(edtName, edtTotalPrice);
        focusTraversal.setListener(() -> {
            String name1 = edtName.getText();
            Double price = Double.parseDouble(edtTotalPrice.getText());
            billItem = new TmpBillItem(name1, price);
            dispose();
        });
        
        getContentPane().add(panel);
        pack();
    }
    
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if (source == btnOk) {
            String name = edtName.getText();
            Double price = Double.parseDouble(edtTotalPrice.getText());
            billItem = new TmpBillItem(name, price);
        } else {
            billItem = null;
        }
        dispose();
    }
    
    public BillItem run() {
        this.setVisible(true);
        return billItem;
    }
}
