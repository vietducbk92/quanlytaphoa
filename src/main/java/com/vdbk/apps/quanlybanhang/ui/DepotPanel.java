/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.ui;

import com.vdbk.apps.quanlybanhang.barcode.BarcodeReader;
import com.vdbk.apps.quanlybanhang.database.DatabaseManager;
import com.vdbk.apps.quanlybanhang.database.DatabaseManager.DatabaseListener;
import com.vdbk.apps.quanlybanhang.database.Item;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author DucNV23
 */
public class DepotPanel extends javax.swing.JPanel implements BarcodeReader.BarcodeListener, DatabaseListener {

    private DepotTable table;
    private JButton addNewItemButton;
//    private JButton addNewItemButton;
//    private JButton editItemButton;
//    private JButton deleteItemButton;
    private JFrame parent;
    private DatabaseManager databaseManager;
    private BarcodeReader barcodeReader;
    //private Item selectedItem;

    public void addComponentsToPane(Container pane) {

        pane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JTextField edtSearch = new JTextField();
        edtSearch.setFont(Constants.FONT_CONTENT);
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 0.8;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        //TODO enable when function available
        edtSearch.setEnabled(false);
        pane.add(edtSearch, c);

        addNewItemButton = new JButton("THÊM HÀNG MỚI");
        addNewItemButton.setFont(Constants.FONT_CONTENT);
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.2;
        c.gridx = 1;
        c.gridy = 0;
        pane.add(addNewItemButton, c);

        table = new DepotTable();
        table.setFont(Constants.FONT_CONTENT);
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 40;      //make this component tall  
        c.weightx = 0.0;
        c.gridwidth = 2;
        c.gridheight = 4;
        c.weighty = 4.0;
        c.gridx = 0;
        c.gridy = 1;
        pane.add(table, c);

        /* addNewItemButton = new JButton("THÊM HÀNG MỚI");
        addNewItemButton.setFont(Constants.FONT_CONTENT);

        editItemButton = new JButton("CHỈNH SỬA THÔNG TIN");
        editItemButton.setFont(Constants.FONT_CONTENT);
        editItemButton.setEnabled(false);
        //   editItemButton.setAlignmentX(CENTER_ALIGNMENT);
        // editItemButton.setSize(50, 200);
        deleteItemButton = new JButton("XÓA");
        deleteItemButton.setFont(Constants.FONT_CONTENT);
        deleteItemButton.setEnabled(false);
        //    deleteItemButton.setAlignmentX(CENTER_ALIGNMENT);

        // Thêm Button vào Panel
        c.insets = new Insets(10, 20, 10, 10);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 1;
        c.weighty = 0.0;
        c.gridheight = 1;
        pane.add(addNewItemButton, c);

        c.insets = new Insets(10, 20, 10, 10);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 2;
        c.weighty = 0.0;
        // buttonContainer.add(Box.createRigidArea(new Dimension(0,25)));
        pane.add(editItemButton, c);

        //buttonContainer.add(Box.createRigidArea(new Dimension(0,25)));
        c.insets = new Insets(10, 20, 10, 10);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 3;
        c.weighty = 0.0;
        pane.add(deleteItemButton, c);

        c.insets = new Insets(10, 20, 10, 10);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 4;
        c.weighty = 1.0;
        pane.add(new Panel(), c);*/
        table.addItemSelectionListener(new DepotTable.ItemSelectionListener() {
            @Override
            public void onItemSelected(Item item) {
                //    selectedItem = item;
                //    editItemButton.setEnabled(true);
                //     deleteItemButton.setEnabled(true);
            }

            @Override
            public void onItemEntered(Item item) {
                updateItem(item);
            }

            @Override
            public void onDeleteItemClicked(Item item) {
                deleteItem(item);
            }
        });

        addNewItemButton.addActionListener((ActionEvent e) -> {
            addNewItem(null);
        });

        /*        editItemButton.addActionListener((ActionEvent e) -> {
            if (selectedItem != null) {
                updateItem(selectedItem);
            }
        });
        deleteItemButton.addActionListener((ActionEvent e) -> {
            if (selectedItem != null) {
                deleteItem(selectedItem);
             //   editItemButton.setEnabled(false);
             //   deleteItemButton.setEnabled(false);
                selectedItem = null;
            }
        });*/
    }

    public DepotPanel(JFrame parent) throws HeadlessException, UnknownHostException {
        this.parent = parent;
        addComponentsToPane(this);
        databaseManager = DatabaseManager.getInstance();
        barcodeReader = BarcodeReader.getInstance();
        databaseManager.addDatabaseListener(this);
        databaseManager.getAllItems(new DatabaseManager.ItemAvailableListener() {
            @Override
            public void onItemAvailable(Item item) {
                table.addNewItem(item);
            }
        });
    }

    public void requestFocus() {
        barcodeReader.clearAllBarcodeListener();
        barcodeReader.addBarcodeListener(this);
    }

    @Override
    public void onBarcodeRead(String barcode) {
        //check item exist in dababase or not
        barcodeReader.removeBarcodeListener(this);
        Item item = databaseManager.getItem(barcode);
        if (item != null) {
            table.scrollToItem(barcode);
            updateItem(item);
        } else {//add new item
            addNewItem(barcode);
        }
        barcodeReader.addBarcodeListener(this);
    }

    private void addNewItem(String barcode) {
        ItemDetailDialog dialog = new ItemDetailDialog(parent, barcode);
        Item ret = dialog.run();
        if (ret != null) {
            databaseManager.insertItem(ret);
        }
    }

    private void updateItem(Item oldItem) {
        ItemDetailDialog dialog = new ItemDetailDialog(parent, oldItem);
        Item ret = dialog.run();
        if (ret != null) {
            databaseManager.update(ret);
        }
    }

    private void deleteItem(Item item) {
        Object[] options = {"Có",
            "Không"};
        int result = JOptionPane.showOptionDialog(
                parent,
                "Bạn có chắc muốn xóa \"" + item.getName() + "\" khỏi kho hàng",
                "Chú ý",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, //do not use a custom Icon
                options, //the titles of buttons
                options[0]);
        if (result == JOptionPane.OK_OPTION) {
            databaseManager.deleteItem(item.getId());
        }
    }

    @Override
    public void onNewItemInserted(Item newItem) {
        table.addNewItem(newItem);
        table.scrollToItem(newItem.getId());
    }

    @Override
    public void onItemUpdated(Item updatedItem) {
        table.update(updatedItem.getId(), updatedItem);
    }

    @Override
    public void onItemDeleted(String id) {
        table.delete(id);
    }
}
