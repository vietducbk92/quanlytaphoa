/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.bill;

import com.vdbk.apps.quanlybanhang.bill.NewBillItem;
import com.vdbk.apps.quanlybanhang.barcode.utils.Utils;
import com.vdbk.apps.quanlybanhang.barcode.BarcodeReader;
import com.vdbk.apps.quanlybanhang.database.DatabaseManager;
import com.vdbk.apps.quanlybanhang.database.Item;
import com.vdbk.apps.quanlybanhang.ui.Constants;
import com.vdbk.apps.quanlybanhang.depot.ItemDetailDialog;
import com.vdbk.apps.quanlybanhang.depot.ItemDialogResult;
import com.vdbk.apps.quanlybanhang.depot.MultiItemDialog;
import com.vdbk.apps.quanlybanhang.ui.JMultilineLabel;
import com.vdbk.apps.quanlybanhang.ui.JTableButton;

import java.awt.*;

import static java.awt.GridBagConstraints.BOTH;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author vietd
 */
public class SellPanel extends javax.swing.JPanel implements BarcodeReader.BarcodeListener, DatabaseManager.DatabaseListener {

    private DatabaseManager dataBaseManager;
    private BarcodeReader barcodeReader;
    private ArrayList<Item> depotItems = new ArrayList<Item>();
    private ArrayList<BillItem> billItems = new ArrayList<BillItem>();
    private JFrame parent;
    private TableRowSorter<TableModel> rowSorter;
    public void requestFocus() {
        barcodeReader.clearAllBarcodeListener();
        barcodeReader.addBarcodeListener(this);
    }

    /**
     * Creates new form NewJPanel
     */
    public SellPanel(JFrame parent) throws UnknownHostException {
        this.parent = parent;
        initComponents();
        dataBaseManager = dataBaseManager.getInstance();
        barcodeReader = BarcodeReader.getInstance();
        dataBaseManager.addDatabaseListener(this);
        //read all item from database
        dataBaseManager.getAllNonBarcodeItems(new DatabaseManager.ItemAvailableListener() {
            @Override
            public void onItemAvailable(Item item) {
                if (item == null) {
                    return;
                }
                DefaultTableModel model = (DefaultTableModel) tableDepot.getModel();
                model.addRow(new Object[]{item.getId(),item.getName(), item.getRetailPrice()});
                depotItems.add(item);
            }
        });
        //select category- all item & non barcode
        cboxCategory.setSelectedIndex(0);
        cboxCategory.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                //delete all item in depot table
                DefaultTableModel dtm = (DefaultTableModel) tableDepot.getModel();
                dtm.setRowCount(0);
                depotItems.clear();
                if (cboxCategory.getSelectedIndex() == 0) {//Non Barcode
                    dataBaseManager.getAllNonBarcodeItems(new DatabaseManager.ItemAvailableListener() {
                        @Override
                        public void onItemAvailable(Item item) {
                            if (item == null) {
                                return;
                            }
                            DefaultTableModel model = (DefaultTableModel) tableDepot.getModel();
                            model.addRow(new Object[]{item.getId(),item.getName(), item.getRetailPrice()});
                            depotItems.add(item);
                        }
                    });
                } else {
                    dataBaseManager.getAllItems(new DatabaseManager.ItemAvailableListener() {
                        @Override
                        public void onItemAvailable(Item item) {
                            if (item == null) {
                                return;
                            }
                            DefaultTableModel model = (DefaultTableModel) tableDepot.getModel();
                            model.addRow(new Object[]{item.getId(),item.getName(), item.getRetailPrice()});
                            depotItems.add(item);
                        }
                    });
                }
            }
        });

        //add item to bill table when double-click to item
        tableDepot.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                JTable table = (JTable) evt.getSource();
                Point point = evt.getPoint();
                int row = table.rowAtPoint(point);
                if (table.getSelectedRow() < 0) {
                    return;
                }
                
                if (evt.getClickCount() == 2) {//double click
                    String barcode = (String) table.getValueAt(row, 0);
                    Item item =  dataBaseManager.getItem(barcode);
                    addItemToBillTable(item);
                }
            }
        });

        //show edited item dialog when double click to bill item
        tableBill.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                JTable table = (JTable) evt.getSource();
                Point point = evt.getPoint();
                if (table.getSelectedRow() < 0) {
                    return;
                }

                if (evt.getClickCount() == 2) {//double click
                    editItemInBillTable(table.getSelectedRow());
                }
            }
        });

        // cap nhat tong tien khi table bill thay doi
        tableBill.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                calculateTotalBill();
            }
        });
        //cap nhat tong tien khi thay doi chiet khau
        edtDiscount.getDocument().addDocumentListener(new DocumentListener() {
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
                calculateTotalBill();
            }
        });

        //tim kiem
        rowSorter = new TableRowSorter<>(tableDepot.getModel());
        tableDepot.setRowSorter(rowSorter);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        cboxCategory = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableBill = new javax.swing.JTable();
        btnAddNewItem = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        edtCustomerName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        edtCustomerPhoneNumber = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tvTotalBill = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        edtDiscount = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        tvTotalBillAfterDiscount = new javax.swing.JLabel();
        btnPay = new javax.swing.JButton();
        bntPayAndPrint = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableDepot = new javax.swing.JTable();
        btnCreateNewBill = new javax.swing.JButton();
        edtSearch = new JTextField();

        setLayout(new java.awt.GridBagLayout());

        cboxCategory.setFont(Constants.FONT_CONTENT);
        cboxCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"KHÔNG CÓ MÃ VẠCH", "TẤT CẢ"}));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(cboxCategory, gridBagConstraints);

        tableBill.setFont(Constants.FONT_CONTENT);
        tableBill.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "SẢN PHẨM", "ĐƠN GIÁ", "SL", "ĐƠN VỊ", "T.TIỀN", ""
                }
        ) {
            Class[] types = new Class[]{
                String.class, java.lang.Double.class, java.lang.Float.class, java.lang.String.class, java.lang.Double.class, JButton.class
            };
            boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }

            @Override
            public Object getValueAt(int row, int column) {
                if (column == 5) {
                    JButton button = new JButton("");
                    button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clear_24.png"))); // NOI18N
                    button.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent arg0) {
                            //delete row bill item
                            deleteItemInBillTable(row);
                        }
                    });
                    return button;
                } else {
                    return super.getValueAt(row, column); //To change body of generated methods, choose Tools | Templates.
                }
            }

        });

        tableBill.getColumnModel().getColumn(5).setCellRenderer(new JTableButton.JTableButtonRenderer());
        tableBill.addMouseListener(new JTableButton.JTableButtonMouseListener(tableBill));
        tableBill.getTableHeader().setReorderingAllowed(false);
        tableBill.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        tableBill.setRowHeight(Constants.TABLE_ROW_HEIGHT);
        tableBill.setRowMargin(Constants.TABLE_ROW_MARGIN);
        tableBill.getTableHeader().setFont(Constants.FONT_CONTENT);
        tableBill.setFont(Constants.FONT_CONTENT);
        tableBill.setShowGrid(true);
        tableBill.getColumnModel().getColumn(0).setMaxWidth(Constants.ITEM_NAME_WIDTH);//ten
        tableBill.getColumnModel().getColumn(0).setMinWidth(Constants.ITEM_NAME_WIDTH);
        tableBill.getColumnModel().getColumn(1).setMaxWidth(Constants.ITEM_PRICE_WIDTH);//gia
        tableBill.getColumnModel().getColumn(1).setMinWidth(Constants.ITEM_PRICE_WIDTH);
        tableBill.getColumnModel().getColumn(2).setMaxWidth(Constants.ITEM_NUMBER_WIDTH);
        tableBill.getColumnModel().getColumn(2).setMinWidth(Constants.ITEM_NUMBER_WIDTH);
        tableBill.getColumnModel().getColumn(3).setMaxWidth(Constants.ITEM_UNIT_WIDTH);
        tableBill.getColumnModel().getColumn(3).setMinWidth(Constants.ITEM_UNIT_WIDTH);
        tableBill.getColumnModel().getColumn(4).setMaxWidth(Constants.ITEM_PRICE_WIDTH);
        tableBill.getColumnModel().getColumn(4).setMinWidth(Constants.ITEM_PRICE_WIDTH);
        tableBill.getColumnModel().getColumn(5).setMaxWidth(Constants.ITEM_BTN_DEL_WIDTH);
        tableBill.getColumnModel().getColumn(5).setMinWidth(Constants.ITEM_BTN_DEL_WIDTH);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(895, 402));
        jScrollPane2.setMinimumSize(new java.awt.Dimension(895, 402));
        jScrollPane2.setViewportView(tableBill);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;

        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane2, gridBagConstraints);

        btnAddNewItem.setFont(Constants.FONT_CONTENT);
        btnAddNewItem.setText("THÊM HÀNG MỚI");
        btnAddNewItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TmpBillItemDialog dialog = new TmpBillItemDialog(parent, "", 0);
                BillItem billItem = dialog.run();
                if (billItem != null) {
                    billItems.add(billItem);
                    DefaultTableModel model = (DefaultTableModel) tableBill.getModel();
                    model.addRow(new Object[]{billItem.getName(), billItem.getUnitPrice(), billItem.getNumber(), billItem.getUnit(), billItem.getTotalPrice()});
                }
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btnAddNewItem, gridBagConstraints);

        jLabel2.setFont(Constants.FONT_CONTENT);
        jLabel2.setText("TÊN");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabel2, gridBagConstraints);

        edtCustomerName.setFont(Constants.FONT_CONTENT);
        //edtCustomerName.setPreferredSize(new java.awt.Dimension(200, 20));
        // edtCustomerName.setMaximumSize(new java.awt.Dimension(200, 20));
        // edtCustomerName.setMinimumSize(new java.awt.Dimension(200, 20));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(edtCustomerName, gridBagConstraints);

        jLabel3.setFont(Constants.FONT_CONTENT);
        jLabel3.setText("SDT");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabel3, gridBagConstraints);

        edtCustomerPhoneNumber.setFont(Constants.FONT_CONTENT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(edtCustomerPhoneNumber, gridBagConstraints);

        jLabel4.setFont(Constants.FONT_CONTENT);
        jLabel4.setText("TỔNG");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabel4, gridBagConstraints);

        tvTotalBill.setFont(Constants.FONT_CONTENT);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tvTotalBill, gridBagConstraints);

        jLabel6.setFont(Constants.FONT_CONTENT);
        jLabel6.setText("C.KHẤU");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabel6, gridBagConstraints);

        edtDiscount.setFont(Constants.FONT_CONTENT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(edtDiscount, gridBagConstraints);

        /*     jLabel7.setFont(Constants.FONT_CONTENT);
        jLabel7.setText("KHÁCH PHẢI TRẢ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabel7, gridBagConstraints);*/
        tvTotalBillAfterDiscount.setFont(Constants.FONT_HEADER);
        tvTotalBillAfterDiscount.setHorizontalAlignment(SwingConstants.CENTER);
        tvTotalBillAfterDiscount.setVerticalAlignment(SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tvTotalBillAfterDiscount, gridBagConstraints);

        tvTotalBillAfterDiscountString = new JMultilineLabel();
        //tvTotalBillAfterDiscountString.setFont(Constants.FONT_CONTENT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tvTotalBillAfterDiscountString, gridBagConstraints);

        btnPay.setFont(Constants.FONT_CONTENT);
        btnPay.setText("THANH TOÁN");
        btnPay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payBill(false);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(btnPay, gridBagConstraints);

        bntPayAndPrint.setFont(Constants.FONT_CONTENT);
        bntPayAndPrint.setEnabled(true);
        bntPayAndPrint.setText("THANH TOÁN & IN HÓA ĐƠN");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        bntPayAndPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payBill(true);
            }
        });
        add(bntPayAndPrint, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        //  gridBagConstraints.weightx = 1.0;
        add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel2, gridBagConstraints);

        jScrollPane3.setPreferredSize(new java.awt.Dimension(300, 45));

        tableDepot.setFont(Constants.FONT_CONTENT);
        tableDepot.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "", "SẢN PHẨM", "ĐƠN GIÁ"
                }
        ) {
            Class[] types = new Class[]{
                java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; //To change body of generated methods, choose Tools | Templates.
            }

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        tableDepot.setRowHeight(Constants.TABLE_ROW_HEIGHT);
        tableDepot.setRowMargin(Constants.TABLE_ROW_MARGIN);
        tableDepot.getTableHeader().setFont(Constants.FONT_CONTENT);
        tableDepot.setFont(Constants.FONT_CONTENT);
        tableDepot.setShowGrid(true);
        tableDepot.getTableHeader().setReorderingAllowed(false);
        //hide column barcode
        tableDepot.getColumnModel().getColumn(0).setMaxWidth(0);
        tableDepot.getColumnModel().getColumn(0).setMinWidth(0);
        tableDepot.getColumnModel().getColumn(1).setMaxWidth(Constants.ITEM_NAME_WIDTH);
        tableDepot.getColumnModel().getColumn(1).setMinWidth(Constants.ITEM_NAME_WIDTH);
        tableDepot.getColumnModel().getColumn(2).setMaxWidth(Constants.ITEM_PRICE_WIDTH);
        tableDepot.getColumnModel().getColumn(2).setMinWidth(Constants.ITEM_PRICE_WIDTH);
        jScrollPane3.setPreferredSize(new java.awt.Dimension(320, 45));
        jScrollPane3.setMinimumSize(new java.awt.Dimension(320, 45));
        jScrollPane3.setViewportView(tableDepot);

        edtSearch.setFont(Constants.FONT_CONTENT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(edtSearch, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane3, gridBagConstraints);

        btnCreateNewBill.setFont(Constants.FONT_CONTENT);
        btnCreateNewBill.setText("TẠO HÓA ĐƠN MỚI");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        btnCreateNewBill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewBill();
            }
        });
        add(btnCreateNewBill, gridBagConstraints);
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnAddNewItem;
    private javax.swing.JButton btnPay;
    private javax.swing.JButton bntPayAndPrint;
    private javax.swing.JButton btnCreateNewBill;
    private javax.swing.JComboBox<String> cboxCategory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel tvTotalBill;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel tvTotalBillAfterDiscount;
    private JMultilineLabel tvTotalBillAfterDiscountString;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tableBill;
    private javax.swing.JTable tableDepot;
    private javax.swing.JTextField edtCustomerName;
    private javax.swing.JTextField edtCustomerPhoneNumber;
    private javax.swing.JTextField edtDiscount;

    private JTextField edtSearch;
    // End of variables declaration 

    private void addItemToBillTable(Item item) {
        if (item == null) {
            return;
        }
        int index = getItemInBill(item.getId());
        if (index >= 0) {//update exist item
            NewBillItem billItem = (NewBillItem) billItems.get(index);
            //show dialog
            BillItemDialog dialog = new BillItemDialog(parent, billItem);
            BillItem editedItem = dialog.run();
            if (editedItem != null) {//edited item
                //update item
                billItems.remove(index);
                billItems.add(index, editedItem);
                tableBill.setValueAt(billItems.get(index).getUnitPrice(), index, 1);
                tableBill.setValueAt(billItems.get(index).getNumber(), index, 2);
                tableBill.setValueAt(billItems.get(index).getTotalPrice(), index, 4);
                scrollToIndex(index);
            }
        } else { //add new item to bill
            NewBillItem billItem = new NewBillItem(item);
            BillItemDialog dialog = new BillItemDialog(parent, billItem);
            BillItem editedItem = dialog.run();
            if (editedItem != null) {//edited item
                //add item to arraylist & jtable
                billItems.add(editedItem);
                DefaultTableModel model = (DefaultTableModel) tableBill.getModel();
                model.addRow(new Object[]{editedItem.getName(), editedItem.getUnitPrice(), editedItem.getNumber(), editedItem.getUnit(), editedItem.getTotalPrice()});
                scrollToIndex(billItems.size()-1);
            }
        }
    }

    private void scrollToIndex(int index) {
        tableBill.setRowSelectionInterval(index, index);
        tableBill.scrollRectToVisible(new Rectangle(tableBill.getCellRect(index, 0, true)));
    }

    private void editItemInBillTable(int row) {
        BillItem billItem = billItems.get(row);
        //show dialog
        BillItem editedItem = null;
        if (billItem instanceof NewBillItem) {
            BillItemDialog dialog = new BillItemDialog(parent, (NewBillItem) billItem);
            editedItem = dialog.run();
        } else {
            TmpBillItemDialog dialog = new TmpBillItemDialog(parent, billItem.getName(), billItem.getTotalPrice());
            editedItem = dialog.run();

        }
        if (editedItem != null) {//edited item
            //update item
            //billItems.get(row).updateTotalAmount(editedItem.getTotalAmount());
            billItems.remove(row);
            billItems.add(row, editedItem);
            tableBill.setValueAt(billItems.get(row).getName(), row, 0);
            tableBill.setValueAt(billItems.get(row).getUnitPrice(), row, 1);
            tableBill.setValueAt(billItems.get(row).getNumber(), row, 2);
            tableBill.setValueAt(billItems.get(row).getTotalPrice(), row, 4);
        }
    }

    private void createNewBill() {
        billItems.clear();
        edtCustomerName.setText("");
        edtCustomerPhoneNumber.setText("");
        edtDiscount.setText("0");
        ((DefaultTableModel) tableBill.getModel()).setRowCount(0);
    }

    private void payBill(boolean printable) {
        //save bill to database
        
        String customerName = edtCustomerName.getText();
        String customerPhoneNumber = edtCustomerPhoneNumber.getText();
        String totalPriceNum = tvTotalBillAfterDiscount.getText();
        String totalPriceStr = tvTotalBillAfterDiscountString.getText();
       
        Bill bill = new Bill(customerName,customerPhoneNumber,billItems,totalPriceNum,totalPriceStr);
        
        if(printable)
            bill.print();
        dataBaseManager.insertNewBill(bill);
        createNewBill();
    }

    private void debtBill() {
        //save dept to database
        createNewBill();
    }

    private void deleteItemInBillTable(int row) {
        billItems.remove(row);
        ((DefaultTableModel) tableBill.getModel()).removeRow(row);
    }

    @Override
    public void onBarcodeRead(String barcode) {
        barcodeReader.removeBarcodeListener(this);
        ArrayList<Item> items = dataBaseManager.getItems(barcode);
        if (!items.isEmpty()) {
            //nhieu mat hang co cung ma vach
            if (items.size() == 1) {
                //check item in bill
                int index = getItemInBill(barcode);
                if (index >= 0) {//item existed in bill
                    NewBillItem billitem = (NewBillItem) (billItems.get(index));
                    float currentNumber = billitem.getNumber();
                    billitem.updateNumber(currentNumber + 1);
                    tableBill.setValueAt(billItems.get(index).getUnitPrice(), index, 1);
                    tableBill.setValueAt(billItems.get(index).getNumber(), index, 2);
                    tableBill.setValueAt(billItems.get(index).getTotalPrice(), index, 4);
                    scrollToIndex(index);
                } else {//new item
                    NewBillItem billItem = new NewBillItem(items.get(0));
                    billItems.add(billItem);
                    DefaultTableModel model = (DefaultTableModel) tableBill.getModel();
                    model.addRow(new Object[]{billItem.getName(), billItem.getUnitPrice(),
                        billItem.getNumber(), billItem.getUnit(), billItem.getTotalPrice()});
                    scrollToIndex(billItems.size()-1);
                }
            } else {
                ItemDialogResult ret = new MultiItemDialog(parent, barcode, items, true).run();
                if (ret == null) {
                    return;
                }
                if (ret.isEnter()) {
                    //check item in bill
                    int index = getItemInBill(ret.item.getId());
                    if (index >= 0) {//item existed in bill
                        NewBillItem billitem = (NewBillItem) (billItems.get(index));
                        float currentNumber = billitem.getNumber();
                        billitem.updateNumber(currentNumber + 1);
                        tableBill.setValueAt(billItems.get(index).getUnitPrice(), index, 1);
                        tableBill.setValueAt(billItems.get(index).getNumber(), index, 2);
                        tableBill.setValueAt(billItems.get(index).getTotalPrice(), index, 4);
                        scrollToIndex(index);
                    } else {//new item
                        NewBillItem billItem = new NewBillItem(ret.item);
                        billItems.add(billItem);
                        DefaultTableModel model = (DefaultTableModel) tableBill.getModel();
                        model.addRow(new Object[]{billItem.getName(), billItem.getUnitPrice(),
                            billItem.getNumber(), billItem.getUnit(), billItem.getTotalPrice()});
                        scrollToIndex(billItems.size()-1);
                    }
                }
            }
        } else {
            ItemDetailDialog dialog = new ItemDetailDialog(parent, barcode);
            ItemDialogResult ret = dialog.run();
            if (ret != null) {
                dataBaseManager.insertItem(ret.item);
                NewBillItem billItem = new NewBillItem(ret.item);
                billItems.add(billItem);
                DefaultTableModel model = (DefaultTableModel) tableBill.getModel();
                model.addRow(new Object[]{billItem.getName(), billItem.getUnitPrice(),
                    billItem.getNumber(), billItem.getUnit(), billItem.getTotalPrice()});
                scrollToIndex(billItems.size()-1);
            }
        }

        barcodeReader.addBarcodeListener(this);
    }

    private void calculateTotalBill() {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

        double totalAmount = 0;
        for (BillItem item : billItems) {
            totalAmount += item.getTotalPrice();
        }
        tvTotalBill.setText(currencyVN.format(Math.ceil(totalAmount)));
        //calculate discount
        double discount = 0;

        try {
            discount = Double.parseDouble(edtDiscount.getText());
        } catch (NumberFormatException e) {
        }
        double totalAmountAfterDiscount = Math.ceil(totalAmount - discount);
        if (totalAmountAfterDiscount > 0) {
            tvTotalBillAfterDiscountString.setText(Utils.numberToString(new BigDecimal(Utils.formatNumberForRead(totalAmountAfterDiscount))));
        } else {
            tvTotalBillAfterDiscountString.setText("");
        }

        tvTotalBillAfterDiscount.setText(currencyVN.format(totalAmountAfterDiscount));

    }

    private int getItemInBill(String barcode) {
        int index = -1;
        for (int i = 0; i < billItems.size(); i++) {
            if (billItems.get(i).getId().equals(barcode)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onNewItemInserted(Item newItem) {
        //add new item to depot table
        if ((newItem.hasBarCode() && cboxCategory.getSelectedIndex() == 1)
                || (!newItem.hasBarCode() && cboxCategory.getSelectedIndex() == 0)) {
            depotItems.add(newItem);
            DefaultTableModel model = (DefaultTableModel) tableDepot.getModel();
            model.addRow(new Object[]{newItem.getId(),newItem.getName(), newItem.getRetailPrice()});
        }
    }

    @Override
    public void onItemUpdated(Item updatedItem) {
        //update item in depot table
        if ((updatedItem.hasBarCode() && cboxCategory.getSelectedIndex() == 1)
                || (!updatedItem.hasBarCode() && cboxCategory.getSelectedIndex() == 0)) {
            for (int i = 0; i < depotItems.size(); i++) {
                if (updatedItem.getId().equals(depotItems.get(i).getId())) {
                    depotItems.remove(i);
                    depotItems.add(i, updatedItem);
                    DefaultTableModel model = (DefaultTableModel) tableDepot.getModel();
                    model.setValueAt(updatedItem.getName(), i, 0);
                    model.setValueAt(updatedItem.getRetailPrice(), i, 1);
                    break;
                }
            }
        }
        //update item in bill table
        int index = getItemInBill(updatedItem.getId());
        if (index >= 0) {
            ((NewBillItem) billItems.get(index)).updateItem(updatedItem);
            tableBill.setValueAt(billItems.get(index).getName(), index, 0);
            tableBill.setValueAt(billItems.get(index).getUnitPrice(), index, 1);
            tableBill.setValueAt(billItems.get(index).getNumber(), index, 2);
            tableBill.setValueAt(billItems.get(index).getUnit(), index, 3);
            tableBill.setValueAt(billItems.get(index).getTotalPrice(), index, 4);
        }
    }

    @Override
    public void onItemDeleted(String id) {
        //remove item in depot table
        for (int i = 0; i < depotItems.size(); i++) {
            if (id.equals(depotItems.get(i).getId())) {
                depotItems.remove(i);
                ((DefaultTableModel) tableDepot.getModel()).removeRow(i);
                break;
            }
        }
        //remove item in bill table
        int index = getItemInBill(id);
        if (index >= 0) {
            billItems.remove(index);
            ((DefaultTableModel) tableBill.getModel()).removeRow(index);
        }
    }

    public void clearSearch() {
        edtSearch.setText("");
        if(rowSorter != null)
            rowSorter.setRowFilter(null);
    }

    @Override
    public void onNewBillInserted(Bill bill) {
        
    }

    @Override
    public void onBillDeleted(String id) {
        
    }
}
