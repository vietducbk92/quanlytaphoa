/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.depot;

import com.vdbk.apps.quanlybanhang.barcode.utils.Utils;
import com.vdbk.apps.quanlybanhang.database.Item;
import com.vdbk.apps.quanlybanhang.ui.Constants;
import com.vdbk.apps.quanlybanhang.ui.FocusTraversal;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.List;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author vietd
 */
public class ItemDetailDialog extends JDialog implements ActionListener {

    private static String ADD_ITEM_TITLE = "THÊM HÀNG MỚI";
    private static String UPDATE_ITEM_TITLE = "SỬA THÔNG TIN";
    private Item item;
    private String barcode;
    private JTextField edtName;//ten
    private JTextField edtOriginPrice;//gia nhap
    private JTextField edtWholeScalePrice;//gia si
    private JTextField edtRetailPrice;// gia le
    private JTextField edtretailMaxNumber;// max number
    private JTextField edtcategory;
    private JTextField edtNote;
    private JTextField edtUnit;
    private JButton btnOk;
    private JButton btnCancel;
    private JButton btnAddNewItemWithBarcode;
    private boolean hasBarcode = false;
    private JFrame parent;
    private boolean updateByBarcodeReader = false;
    private ItemDialogResult result = null;

    public ItemDetailDialog(JFrame parent, String barcode) {
        super(parent, ADD_ITEM_TITLE + (barcode == null ? "" : "-" + barcode), true);
        hasBarcode = (barcode != null);
        //barcode 
        if (!hasBarcode) {
            this.barcode = System.currentTimeMillis() + "";
        } else {
            this.barcode = barcode;
        }
        this.parent = parent;
        addComponentToDialog();
        Point loc = parent.getLocation();
        setLocation(loc.x + parent.getWidth() / 2 - getWidth() / 2, loc.y + parent.getHeight() / 2 - getHeight() / 2);

    }

    public ItemDetailDialog(JFrame parent, Item item, boolean updateByBarcodeReader) {
        super(parent, UPDATE_ITEM_TITLE + ((item != null && item.hasBarCode()) ? "-" + item.getId() : ""), true);
        if (item == null) {
            return;
        }
        this.updateByBarcodeReader = updateByBarcodeReader;
        this.parent = parent;
        hasBarcode = item.hasBarCode();
        barcode = item.getId();
        addComponentToDialog();
        //fill item data to UI

        edtName.setText(item.getName());
        edtNote.setText(item.getNote());
        edtOriginPrice.setText(Utils.fmt(item.getOriginPrice()));
        edtRetailPrice.setText(Utils.fmt(item.getRetailPrice()));
        edtUnit.setText(item.getUnit());
        edtWholeScalePrice.setText(Utils.fmt(item.getWholeScalePrice()));
        edtcategory.setText(item.getCategory());
        edtretailMaxNumber.setText(item.getRetailMaxNumber() + "");
        Point loc = parent.getLocation();
        setLocation(loc.x + parent.getWidth() / 2 - getWidth() / 2, loc.y + parent.getHeight() / 2 - getHeight() / 2);
    }

    private void addComponentToDialog() {
        JPanel panel = new JPanel();
        JLabel label;
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 20, 2, 2);

        int row = 0;
        //show barcode
        label = new JLabel("Barcode");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);
        String baseBarcode = barcode.split("_")[0];
        label = new JLabel(baseBarcode);
        label.setFont(Constants.FONT_CONTENT);
        gbc.gridwidth = 2;
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(label, gbc);
        if (updateByBarcodeReader) {
            btnAddNewItemWithBarcode = new JButton("THÊM HÀNG CÙNG MÃ");
            label.setFont(Constants.FONT_CONTENT);
            gbc.gridwidth = 2;
            gbc.gridx = 3;
            gbc.gridy = row;
            panel.add(btnAddNewItemWithBarcode, gbc);
            btnAddNewItemWithBarcode.addActionListener(this);
        }
        row++;

        //insert name 
        label = new JLabel("Tên hàng");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);
        edtName = new JTextField(30);
        edtName.setFont(Constants.FONT_CONTENT);
        gbc.gridwidth = 4;
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(edtName, gbc);
        row++;

        //donvi
        label = new JLabel("Đơn vị ");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);
        edtUnit = new JTextField();
        edtUnit.setText("cái");
        edtUnit.setFont(Constants.FONT_CONTENT);
        gbc.gridwidth = 2;
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(edtUnit, gbc);
        row++;

        //insert origin price
        label = new JLabel("Giá nhập");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);
        edtOriginPrice = new JTextField(30);
        edtOriginPrice.setFont(Constants.FONT_CONTENT);
        gbc.gridwidth = 4;
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(edtOriginPrice, gbc);
        row++;
        //gia le
        label = new JLabel("Giá bán lẻ");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);

        edtRetailPrice = new JTextField(30);
        edtRetailPrice.setFont(Constants.FONT_CONTENT);
        gbc.gridwidth = 4;
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(edtRetailPrice, gbc);
        row++;
        //gia si
        label = new JLabel("Bán sỉ");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);

        label = new JLabel("SL");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(label, gbc);

        edtretailMaxNumber = new JTextField(5);
        edtretailMaxNumber.setFont(Constants.FONT_CONTENT);
        gbc.gridwidth = 1;
        gbc.gridx = 2;
        gbc.gridy = row;
        panel.add(edtretailMaxNumber, gbc);

        label = new JLabel("Giá");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.gridx = 3;
        gbc.gridy = row;
        panel.add(label, gbc);

        edtWholeScalePrice = new JTextField(10);
        edtWholeScalePrice.setFont(Constants.FONT_CONTENT);
        gbc.gridwidth = 1;
        gbc.gridx = 4;
        gbc.gridy = row;
        panel.add(edtWholeScalePrice, gbc);
        row++;

        //danh muc
        label = new JLabel("Danh mục ");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);
        edtcategory = new JTextField(30);
        edtcategory.setFont(Constants.FONT_CONTENT);
        gbc.gridwidth = 4;
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(edtcategory, gbc);
        row++;

        //ghi chu
        label = new JLabel("Ghi chú ");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);
        edtNote = new JTextField(30);
        edtNote.setFont(Constants.FONT_CONTENT);
        gbc.gridwidth = 4;
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(edtNote, gbc);
        row++;

        //button
        JLabel spacer = new JLabel(" ");
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(spacer, gbc);
        row++;

        btnOk = new JButton("LƯU");
        btnOk.setFont(Constants.FONT_CONTENT);
        btnOk.addActionListener(this);
        gbc.gridwidth = 2;
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(btnOk, gbc);
        btnCancel = new JButton("HỦY");
        btnCancel.setFont(Constants.FONT_CONTENT);
        btnCancel.addActionListener(this);
        gbc.gridwidth = 1;
        gbc.gridx = 4;
        gbc.gridy = row;
        panel.add(btnCancel, gbc);
        row++;

        FocusTraversal traversal = new FocusTraversal(
                edtName,
                edtUnit,
                edtOriginPrice,
                edtRetailPrice,
                edtretailMaxNumber,
                edtWholeScalePrice,
                edtcategory,
                edtNote
        );
        traversal.setListener(() -> {
            saveItem();
            dispose();
        });

        getContentPane().add(panel);
        pack();
    }

    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if (source == btnOk) {
            saveItem();
            result = new ItemDialogResult(ItemDialogResult.RESULT_UPDATE, item);
        } else if (source == btnAddNewItemWithBarcode) {
            item = null;
            result = new ItemDialogResult(ItemDialogResult.RESULT_NEW_ITEM, null);
        } else {
            result = null;
            item = null;
        }
        dispose();
    }

    public ItemDialogResult run() {
        this.setVisible(true);
        return result;
    }

    private void showNoticeDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Chú ý", JOptionPane.WARNING_MESSAGE);
    }

    private void saveItem() {
        item = new Item();
        item.setId(barcode);
        item.setCategory(edtcategory.getText());
        if (edtName.getText().isEmpty()) {
            showNoticeDialog("Tên hàng không đươc để trống");

            return;
        } else {
            item.setName(edtName.getText());
        }
        item.setNote(edtNote.getText());
        try {
            item.setOriginPrice(Double.parseDouble(edtOriginPrice.getText()));
            item.setRetailPrice(Double.parseDouble(edtRetailPrice.getText()));
        } catch (NumberFormatException | NullPointerException e) {
            showNoticeDialog("Nhập lại giá nhập/bán lẻ");
            return;
        }

        item.setUnit(edtUnit.getText());
        try {
            item.setWholeScalePrice(Double.parseDouble(edtWholeScalePrice.getText()));
        } catch (NumberFormatException | NullPointerException e) {
            item.setWholeScalePrice(0);
        }
        if (!edtretailMaxNumber.getText().isEmpty()) {
            item.setRetailMaxNumber(Integer.parseInt(edtretailMaxNumber.getText()));
        }
        item.setHasBarcode(hasBarcode ? 1 : 0);
    }
}
