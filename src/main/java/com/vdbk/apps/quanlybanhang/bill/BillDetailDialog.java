/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.bill;

import com.vdbk.apps.quanlybanhang.depot.*;
import com.vdbk.apps.quanlybanhang.bill.*;
import com.vdbk.apps.quanlybanhang.barcode.utils.Utils;
import com.vdbk.apps.quanlybanhang.database.Item;
import com.vdbk.apps.quanlybanhang.ui.Constants;
import com.vdbk.apps.quanlybanhang.ui.FocusTraversal;
import com.vdbk.apps.quanlybanhang.ui.JTableButton;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author vietd
 */
//show khi thêm hàng không có mã vạch vào kho
//hoặc là show khi double click vào hàng trong hóa đơn để sửa số lượng/giá bán
public class BillDetailDialog extends JDialog {

    private static String TITLE = "HÓA ĐƠN";
    private Bill bill;
    private JFrame parent;
    private JButton btnPrintBill;
    private JTable tableBillContent;

    public BillDetailDialog(JFrame parent, Bill bill) {
        super(parent, TITLE, true);
        this.parent = parent;
        this.bill = bill;
        setPreferredSize(new Dimension(550, 600));
        addComponentToDialog();
        Point loc = parent.getLocation();
        setLocation(loc.x + parent.getWidth() / 2 - getWidth() / 2, loc.y + parent.getHeight() / 2 - getHeight() / 2);
        //fill item to UI
        DefaultTableModel model = (DefaultTableModel) tableBillContent.getModel();
        String[] items = bill.getContent().split("/");
        for (String item : items) {
            String[] itemDetail = item.split("-");
            String name = itemDetail[0].toLowerCase();
            String unitPrice = itemDetail[1];
            String number = itemDetail[2];
            String unit = itemDetail[3].toLowerCase();
            String price = itemDetail[4];

            model.addRow(new Object[]{name,unitPrice,number,unit,price});
        }
        //action
        btnPrintBill.addActionListener((ActionEvent e) -> {
            bill.print();
            dispose();
        });
    }

    private void addComponentToDialog() {
        JPanel panel = new JPanel();

        tableBillContent = new JTable();
        tableBillContent.setFont(Constants.FONT_CONTENT);
        tableBillContent.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "SẢN PHẨM", "ĐƠN GIÁ", "SL", "ĐƠN VỊ", "T.TIỀN",}
        ) {
            Class[] types = new Class[]{
                String.class, String.class, String.class, String.class, String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }

        });

        tableBillContent.getTableHeader().setReorderingAllowed(false);
        tableBillContent.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableBillContent.setRowHeight(Constants.TABLE_ROW_HEIGHT);
        tableBillContent.setRowMargin(Constants.TABLE_ROW_MARGIN);
        tableBillContent.getTableHeader().setFont(Constants.FONT_CONTENT);
        tableBillContent.setFont(Constants.FONT_CONTENT);
        tableBillContent.setShowGrid(true);
        tableBillContent.getColumnModel().getColumn(0).setMaxWidth(Constants.ITEM_NAME_WIDTH);//ten
        tableBillContent.getColumnModel().getColumn(0).setMinWidth(Constants.ITEM_NAME_WIDTH);
        tableBillContent.getColumnModel().getColumn(1).setMaxWidth(Constants.ITEM_PRICE_WIDTH);//gia
        tableBillContent.getColumnModel().getColumn(1).setMinWidth(Constants.ITEM_PRICE_WIDTH);
        tableBillContent.getColumnModel().getColumn(2).setMaxWidth(Constants.ITEM_NUMBER_WIDTH);
        tableBillContent.getColumnModel().getColumn(2).setMinWidth(Constants.ITEM_NUMBER_WIDTH);
        tableBillContent.getColumnModel().getColumn(3).setMaxWidth(Constants.ITEM_UNIT_WIDTH);
        tableBillContent.getColumnModel().getColumn(3).setMinWidth(Constants.ITEM_UNIT_WIDTH);
        tableBillContent.getColumnModel().getColumn(4).setMaxWidth(Constants.ITEM_PRICE_WIDTH);
        tableBillContent.getColumnModel().getColumn(4).setMinWidth(Constants.ITEM_PRICE_WIDTH);

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        //khach hang
        String customerName = bill.getCustomerName().isEmpty() ? "Khách lẻ" : bill.getCustomerName();
        JLabel label = new JLabel("Khách hàng: " + customerName);
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label, gbc);
        //sdt
        label = new JLabel("SDT: " + bill.getCustomerPhoneNumber());
        label.setFont(Constants.FONT_CONTENT);
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(label, gbc);
        //date
        String timeStamp = new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Timestamp(Long.parseLong(bill.getId())));
        label = new JLabel("Ngày: " + timeStamp);
        label.setFont(Constants.FONT_CONTENT);
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(label, gbc);

        //bill table
        JScrollPane jScrollPane = new javax.swing.JScrollPane();
        jScrollPane.setPreferredSize(new java.awt.Dimension(545, 300));
        jScrollPane.setMinimumSize(new java.awt.Dimension(545, 300));
        jScrollPane.setViewportView(tableBillContent);
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 3;

        panel.add(jScrollPane, gbc);
        
        //tong tien
        label = new JLabel("Tổng tiền: " + bill.getTotalPriceNum());
        label.setFont(Constants.FONT_CONTENT);
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(label, gbc);
        label = new JLabel(bill.getTotalPriceStr());
        label.setFont(Constants.FONT_CONTENT);
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(label, gbc);

        btnPrintBill = new JButton("IN HÓA ĐƠN");
        btnPrintBill.setFont(Constants.FONT_CONTENT);
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(btnPrintBill, gbc);
        getContentPane().add(panel);
        pack();
    }

    public ItemDialogResult run() {
        this.setVisible(true);
        return null;
    }
}
