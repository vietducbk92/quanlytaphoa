/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.bill;

import com.vdbk.apps.quanlybanhang.barcode.BarcodeReader;
import com.vdbk.apps.quanlybanhang.bill.Bill;
import com.vdbk.apps.quanlybanhang.database.DatabaseManager;
import com.vdbk.apps.quanlybanhang.database.DatabaseManager.DatabaseListener;
import com.vdbk.apps.quanlybanhang.database.Item;
import com.vdbk.apps.quanlybanhang.ui.Constants;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author DucNV23
 */
public class BillPanel extends javax.swing.JPanel implements DatabaseListener {

    private JTable billTable;
    private JFrame parent;
    private DatabaseManager databaseManager;
    private BarcodeReader barcodeReader;
    public void addComponentsToPane(Container pane) {

        pane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        pane.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        
        billTable = new JTable();
        billTable.setFont(Constants.FONT_CONTENT);
        billTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "","NGÀY", "KHÁCH HÀNG", "SDT","THÀNH TIỀN"
                }
        ) {
            Class[] types = new Class[]{
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; //To change body of generated methods, choose Tools | Templates.
            }

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        billTable.setRowHeight(Constants.TABLE_ROW_HEIGHT);
        billTable.setRowMargin(Constants.TABLE_ROW_MARGIN);
        billTable.getTableHeader().setFont(Constants.FONT_CONTENT);
        billTable.setFont(Constants.FONT_CONTENT);
        billTable.setShowGrid(true);
        billTable.getTableHeader().setReorderingAllowed(false);
        //hide column barcode
        billTable.getColumnModel().getColumn(0).setMaxWidth(0);
        billTable.getColumnModel().getColumn(0).setMinWidth(0);
        billTable.getColumnModel().getColumn(1).setMaxWidth(Constants.ITEM_NAME_WIDTH);
        billTable.getColumnModel().getColumn(1).setMinWidth(Constants.ITEM_NAME_WIDTH);
        billTable.getColumnModel().getColumn(2).setMaxWidth(Constants.ITEM_NAME_WIDTH);
        billTable.getColumnModel().getColumn(2).setMinWidth(Constants.ITEM_NAME_WIDTH);
        billTable.getColumnModel().getColumn(3).setMaxWidth(Constants.ITEM_NAME_WIDTH);
        billTable.getColumnModel().getColumn(3).setMinWidth(Constants.ITEM_NAME_WIDTH);


        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(billTable);
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(scrollPane, gridBagConstraints);
       
    }

    public BillPanel(JFrame parent) throws HeadlessException, UnknownHostException {
        this.parent = parent;
        addComponentsToPane(this);
        databaseManager = DatabaseManager.getInstance();
        databaseManager.addDatabaseListener(this);
        barcodeReader = BarcodeReader.getInstance();
        //read all value in database and insert to table 
        databaseManager.getAllBills((Bill item) -> {
            DefaultTableModel model = (DefaultTableModel) billTable.getModel();
            String timeStamp = new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Timestamp(Long.parseLong(item.getId())));
            model.addRow(new Object[]{item.getId(),timeStamp,item.getCustomerName(),item.getCustomerPhoneNumber(),item.getTotalPriceNum()});
        });
        
                //add item to bill table when double-click to item
        billTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                JTable table = (JTable) evt.getSource();
                Point point = evt.getPoint();
                int row = table.rowAtPoint(point);
                if (table.getSelectedRow() < 0) {
                    return;
                }
                
                if (evt.getClickCount() == 2) {//double click
                    String id = table.getValueAt(row, 0).toString();
                    Bill item =  databaseManager.getBill(id);
                    //show bill item dialog to print/preview bill
                    if(item != null)
                        new BillDetailDialog(parent, item).run();
                    else
                        System.out.println("Bill is null");
                }
            }
        });
    }
    
    public void requestFocus(){
        barcodeReader.clearAllBarcodeListener();
    }

    @Override
    public void onNewItemInserted(Item newItem) {
       
    }

    @Override
    public void onItemUpdated(Item updatedItem) {
        
    }

    @Override
    public void onItemDeleted(String id) {
        
    }

    @Override
    public void onNewBillInserted(Bill item) {
        DefaultTableModel model = (DefaultTableModel) billTable.getModel();
        String timeStamp = new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Timestamp(Long.parseLong(item.getId())));
        model.insertRow(0,new Object[]{item.getId(),timeStamp,item.getCustomerName(),item.getCustomerPhoneNumber(),item.getTotalPriceNum()});
    }

    @Override
    public void onBillDeleted(String id) {
        
    }
}
