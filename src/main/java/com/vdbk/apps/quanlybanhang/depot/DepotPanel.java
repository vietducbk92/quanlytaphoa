/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.depot;

import com.vdbk.apps.quanlybanhang.barcode.BarcodeReader;
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
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author DucNV23
 */
public class DepotPanel extends javax.swing.JPanel implements BarcodeReader.BarcodeListener, DatabaseListener {

    private DepotTable table;
    private JButton btnAddNewItem;
//    private JButton addNewItemButton;
//    private JButton editItemButton;
//    private JButton deleteItemButton;
    private JFrame parent;
    private DatabaseManager databaseManager;
    private BarcodeReader barcodeReader;
    JTextField edtSearch;
    //private Item selectedItem;

    public void addComponentsToPane(Container pane) {

        pane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        edtSearch = new JTextField();
        edtSearch.setFont(Constants.FONT_CONTENT);
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 0.8;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        //TODO enable when function available
        pane.add(edtSearch, c);

        btnAddNewItem = new JButton("THÊM HÀNG MỚI");
        btnAddNewItem.setFont(Constants.FONT_CONTENT);
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.2;
        c.gridx = 1;
        c.gridy = 0;
        pane.add(btnAddNewItem, c);

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

        table.addItemSelectionListener(new DepotTable.ItemSelectionListener() {
            @Override
            public void onItemSelected(Item item) {
                //    selectedItem = item;
                //    editItemButton.setEnabled(true);
                //     deleteItemButton.setEnabled(true);
            }

            @Override
            public void onItemEntered(String id) {
                Item item = databaseManager.getItem(id);
                if(item != null)
                    updateItem(item, false);

            }

            @Override
            public void onDeleteItemClicked(String id) {
                Item item = databaseManager.getItem(id);
                if(item != null)
                    deleteItem(item);
            }
        });

        btnAddNewItem.addActionListener((ActionEvent e) -> {
            addNewItem(null);
        });
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
        
        //search
        //tim kiem
        TableRowSorter<TableModel> rowSorter = table.getRowSorter();
        edtSearch.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = edtSearch.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text.toUpperCase()));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = edtSearch.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text.toUpperCase()));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        ArrayList<Item> items = databaseManager.getItems(barcode);
        if (!items.isEmpty()) {
            if (items.size() == 1) {
                table.scrollToItem(barcode);
                updateItem(items.get(0), true);
            } else {//open multi dialog
                if (items.size() > 1) {
                    MultiItemDialog dialog = new MultiItemDialog(parent, barcode, items, false);
                    ItemDialogResult result = dialog.run();
                    if(result == null)
                        return;
                    if (result.isAddNewItem()) {
                        addNewItem(barcode+"_"+System.currentTimeMillis());
                    } else if (result.isDelete()) {
                        databaseManager.deleteItem(result.item.getId());
                    } else if (result.isEnter()) {
                        table.scrollToItem(result.item.getId());
                        updateItem(result.item, false);
                    }
                }
            }
        } else {//add new item
            addNewItem(barcode);
        }
        barcodeReader.addBarcodeListener(this);
    }

    private void addNewItem(String barcode) {
        ItemDetailDialog dialog = new ItemDetailDialog(parent, barcode);
        ItemDialogResult ret = dialog.run();
        if (ret != null && ret.item != null) {
            databaseManager.insertItem(ret.item);
        }
    }

    private void updateItem(Item oldItem, boolean updateByBarcodeReader) {
        //TEST
        ItemDetailDialog dialog = new ItemDetailDialog(parent, oldItem, updateByBarcodeReader);
        ItemDialogResult ret = dialog.run();
        if (ret != null) {
            if(ret.isUpdateItem())
                databaseManager.update(ret.item);
            else if(ret.isAddNewItem()){// tao mat hang co chung barcode
                addNewItem(ret.item.getId()+"_"+System.currentTimeMillis());
            }
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
