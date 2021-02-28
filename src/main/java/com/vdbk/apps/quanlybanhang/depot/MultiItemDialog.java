/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.depot;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class MultiItemDialog extends JDialog {

    private static String TITLE = "HÀNG CÙNG MÃ VẠCH";
    private BillItem billItem = null;
    private ArrayList<Item> items;
    private Item choseItem = null;
    private String barcode;
    private JButton btnAddNewItem;
    private JButton btnCancel;
    private JTable tableItems;
    private ItemDialogResult result = null;
    private JFrame parent;
    private boolean openInBill = false;

    public MultiItemDialog(JFrame parent, String barcode, ArrayList<Item> items, boolean openInBill) {
        super(parent, TITLE, true);
        this.items = items;
        this.barcode = barcode;
        this.parent = parent;
        this.openInBill = openInBill;
        setPreferredSize(new Dimension(500, 450));
        addComponentToDialog();
        Point loc = parent.getLocation();
        setLocation(loc.x + parent.getWidth() / 2 - getWidth() / 2, loc.y + parent.getHeight() / 2 - getHeight() / 2);
        //fill item to UI
        for (Item item : items) {
            DefaultTableModel model = (DefaultTableModel) tableItems.getModel();
            model.addRow(new Object[]{item.getName(), item.getRetailPrice()});
        }
        //action
        //double click item
        tableItems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                JTable table = (JTable) evt.getSource();
                Point point = evt.getPoint();
                if (table.getSelectedRow() < 0) {
                    return;
                }

                if (evt.getClickCount() == 2) {//double click
                    Item item = items.get(table.getSelectedRow());
                    result = new ItemDialogResult(ItemDialogResult.RESULT_ENTER, item);
                    dispose();
                }
            }
        });

        btnAddNewItem.addActionListener((ActionEvent e) -> {
            result = new ItemDialogResult(ItemDialogResult.RESULT_NEW_ITEM, null);
            dispose();
        });
    }

    private void addComponentToDialog() {
        JPanel panel = new JPanel();
        tableItems = new JTable();
        tableItems.setFont(Constants.FONT_CONTENT);
        tableItems.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "SẢN PHẨM", "ĐƠN GIÁ", ""
                }
        ) {
            Class[] types = new Class[]{
                String.class, java.lang.Double.class, JButton.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }

            @Override
            public Object getValueAt(int row, int column) {
                if (column == 2) {
                    JButton button = new JButton("");
                    button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clear_24.png"))); // NOI18N
                    button.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent arg0) {
                            if (openInBill) {
                                return;
                            }
                            //delete item
                            Item item = items.get(row);
                            Object[] options = {"Có",
                                "Không"};
                            int ret = JOptionPane.showOptionDialog(
                                    parent,
                                    "Bạn có chắc muốn xóa \"" + item.getName() + "\" khỏi kho hàng",
                                    "Chú ý",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null, //do not use a custom Icon
                                    options, //the titles of buttons
                                    options[0]);
                            if (ret == JOptionPane.OK_OPTION) {
                                result = new ItemDialogResult(ItemDialogResult.RESULT_DELETE, item);
                                dispose();
                            }
                        }
                    });
                    return button;
                } else {
                    return super.getValueAt(row, column); //To change body of generated methods, choose Tools | Templates.
                }
            }

        });

        tableItems.getColumnModel().getColumn(2).setCellRenderer(new JTableButton.JTableButtonRenderer());
        tableItems.addMouseListener(new JTableButton.JTableButtonMouseListener(tableItems));
        tableItems.getTableHeader().setReorderingAllowed(false);
        tableItems.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableItems.setRowHeight(Constants.TABLE_ROW_HEIGHT);
        tableItems.setRowMargin(Constants.TABLE_ROW_MARGIN);
        tableItems.getTableHeader().setFont(Constants.FONT_CONTENT);
        tableItems.setFont(Constants.FONT_CONTENT);
        tableItems.setShowGrid(true);
//        tableItems.getColumnModel().getColumn(0).setMaxWidth(Constants.ITEM_NAME_WIDTH);//ten
//        tableItems.getColumnModel().getColumn(0).setMinWidth(Constants.ITEM_NAME_WIDTH);
        tableItems.getColumnModel().getColumn(1).setMaxWidth(Constants.ITEM_PRICE_WIDTH);//gia
        tableItems.getColumnModel().getColumn(1).setMinWidth(Constants.ITEM_PRICE_WIDTH);
        //hide column delete when open in bill
        tableItems.getColumnModel().getColumn(2).setMaxWidth(openInBill ? 0 : Constants.ITEM_BTN_DEL_WIDTH);//delete
        tableItems.getColumnModel().getColumn(2).setMinWidth(openInBill ? 0 : Constants.ITEM_BTN_DEL_WIDTH);

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        //barcode
        JLabel label = new JLabel("Barcode");
        label.setFont(Constants.FONT_CONTENT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label, gbc);
        label = new JLabel(barcode);
        label.setFont(Constants.FONT_TITLE);
        gbc.weightx = 1;
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(label, gbc);
        //item table
        JScrollPane jScrollPane = new javax.swing.JScrollPane();
        jScrollPane.setViewportView(tableItems);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        jScrollPane.setPreferredSize(new java.awt.Dimension(400, 300));
        jScrollPane.setMinimumSize(new java.awt.Dimension(400, 300));
        panel.add(jScrollPane, gbc);

        btnAddNewItem = new JButton("THÊM HÀNG CÙNG MÃ VẠCH");
        btnAddNewItem.setFont(Constants.FONT_CONTENT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        if (!openInBill) {
            panel.add(btnAddNewItem, gbc);
        }

        getContentPane().add(panel);
        pack();
    }

    public ItemDialogResult run() {
        this.setVisible(true);
        return result;
    }
}
