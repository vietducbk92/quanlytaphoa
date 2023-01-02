/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.bill;

import com.vdbk.apps.quanlybanhang.barcode.utils.Utils;
import com.vdbk.apps.quanlybanhang.ui.Constants;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import org.bson.Document;

/**
 *
 * @author vietd
 */
public class Bill implements Printable {

    private String id;
    private String customerName;
    private String customerPhoneNumber;
    private String content;
    private String totalPriceNum;
    private String totalPriceStr;

    public static String KEY_ID = "_id";
    public static String KEY_CUSTOMER_NAME = "_customer_name";
    public static String KEY_CUSTOMER_PHONE_NUMBER = "_customer_phone";
    public static String KEY_CONTENT = "_content";
    public static String KEY_TOTAL_PRICE_NUM = "_total_price_num";
    public static String KEY_TOTAL_PRICE_STR = "_total_price_str";

    private boolean printHeader = false;
    private boolean printEnding = false;
    private int printStartIndex = 0;
    private int PRINT_SIZE = 10;
    private ArrayList<String> items = new ArrayList<>();

    public Bill(String customerName, String phoneNumber, ArrayList<BillItem> billItems, String totalPriceNum, String totalPriceStr) {
        id = System.currentTimeMillis()+"";
        this.customerName = customerName;
        this.customerPhoneNumber = phoneNumber;
        this.totalPriceNum = totalPriceNum;
        this.totalPriceStr = totalPriceStr;
        this.content = "";
        for (BillItem item : billItems) {
            this.content = this.content + item.getName() + "-" + Utils.fmt(item.getUnitPrice()) + "-" + Utils.fmt(item.getNumber()) + "-" + item.getUnit() + "-" + Utils.fmt(item.getTotalPrice()) + "/";
        }
    }
    
    public Bill(Document obj) {
        this.id = ((String) obj.get(KEY_ID));
        this.customerName = ((String) obj.get(KEY_CUSTOMER_NAME));
        this.customerPhoneNumber = ((String) obj.get(KEY_CUSTOMER_PHONE_NUMBER));
        this.totalPriceNum = ((String) obj.get(KEY_TOTAL_PRICE_NUM));
        this.totalPriceStr = ((String) obj.get(KEY_TOTAL_PRICE_STR));
        this.content = ((String) obj.get(KEY_CONTENT));
    }

    public void print() {
        PrinterJob job = PrinterJob.getPrinterJob();

        PageFormat format = new PageFormat();
        job.validatePage(format);
        job.setPrintable(this, format);
//        boolean ok = job.printDialog();
//        if (ok)
        {
            try {
              //  job.print();
                printHeader(job);
                items = new ArrayList<>();
                Collections.addAll(items, getItems());
                int numberOfPage = items.size() / PRINT_SIZE;
                for(int i=0;i<=numberOfPage;i++)
                    printContent(job,i*PRINT_SIZE);
                printEnding(job);
            } catch (PrinterException ex) {
                ex.printStackTrace();
                /* The job did not successfully complete */
            }
        }
    }
    private void printHeader(PrinterJob job) throws PrinterException {
        printHeader = true;
        printEnding = false;
        job.print();
    }
    private void printEnding(PrinterJob job) throws PrinterException {
        printHeader = false;
        printEnding = true;
        job.print();
    }
    private void printContent(PrinterJob job, int index) throws PrinterException {
        printHeader = false;
        printEnding = false;
        printStartIndex = index;
        job.print();
    }
    private String[] getItems(){
        return getContent().split("/");
    }

    public int print(Graphics g, PageFormat pf, int page) throws
            PrinterException {
        System.out.println("Print pagge: "+page) ;
        if (page > 0) {
            /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }
        int x = 0;
        int y = 20;
        int lineHeight = 0;
        int padding = 10;

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        int pageWidth = (int) pf.getImageableY() * 2 - padding;
        //draw HEADER
        //store name
        Font font;
        if(printHeader) {
            font = new Font("Serif", Font.BOLD, 9);
            lineHeight = drawText(Constants.STORE_NAME, y, g2d, font, pageWidth, CENTER);
            y += lineHeight;
            //store phone
            font = new Font("Serif", Font.PLAIN, 8);
            lineHeight = drawText(Constants.STORE_PHONE_NUMBER, y, g2d, font, pageWidth, CENTER);
            y += lineHeight;
            //store address
            lineHeight = drawText(Constants.STORE_ADDRESS, y, g2d, font, pageWidth, CENTER);
            y += lineHeight;
            //draw line
            g.drawLine(0, y, pageWidth, y);
            y += lineHeight;
            //draw bill tilte
            font = new Font("Serif", Font.BOLD, 9);
            lineHeight = drawText("HÓA ĐƠN BÁN HÀNG", y, g2d, font, pageWidth, CENTER);
            y += lineHeight;
            //draw date
            font = new Font("Serif", Font.PLAIN, 9);
            String timeStamp = new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Timestamp(Long.parseLong(getId())));
            lineHeight = drawText(timeStamp, y, g2d, font, pageWidth, CENTER);
            y += lineHeight;
            //draw customer name
            String text = getCustomerName().isEmpty() ? "Khách lẻ" : getCustomerName();
            lineHeight = drawText("Khách hàng: " + text, y, g2d, font, pageWidth, LEFT);
            y += lineHeight;
            //draw customer phone
            if (!customerPhoneNumber.isEmpty()) {
                lineHeight = drawText("SDT: " + getCustomerPhoneNumber(), y, g2d, font, pageWidth, LEFT);
                y += lineHeight;
            }
            g.drawLine(0, y, pageWidth, y);
            y += lineHeight;

        } else if(printEnding){
//            g.drawLine(0, y, pageWidth, y);
//            y += lineHeight;
            //Draw total price
            font = new Font("Serif", Font.BOLD, 12);
            lineHeight = drawText("Tổng:", y, g2d, font, pageWidth, LEFT);
            lineHeight = drawText(getTotalPriceNum(), y, g2d, font, pageWidth, RIGHT);
            y += lineHeight;
            font = new Font("Serif", Font.ITALIC, 9);
            lineHeight = drawText(getTotalPriceStr(), y, g2d, font, pageWidth, LEFT);
            y += lineHeight * 2;

            drawText("Cảm ơn quý khách", y, g2d, font, pageWidth, CENTER);
        } else {
            //draw content
            font = new Font("Serif", Font.PLAIN, 9);
            //  String[] items = getContent().split("/");
            int endIndex = Math.min(printStartIndex + PRINT_SIZE, items.size());
            for (int i = printStartIndex; i < endIndex; i++) {
                String item = items.get(i);
                String[] itemDetail = item.split("-");
                String name = itemDetail[0].toLowerCase();
                String unitPrice = itemDetail[1];
                String number = itemDetail[2];

                String unit = itemDetail[3].toLowerCase();
                String price = itemDetail[4];
                String nameStr = (i+1)+"-"+name;
                lineHeight = drawText(nameStr.toUpperCase(), y, g2d, font, pageWidth, LEFT);
                y += lineHeight;
                lineHeight = drawText(unitPrice + "x" + number + " " + unit, y, g2d, font, pageWidth, LEFT);
                drawText(price, y, g2d, font, pageWidth, RIGHT);
                y += 5;
                g.drawLine(0, y, pageWidth, y);
                y += lineHeight;
            }

        }
        return PAGE_EXISTS;
    }

    private int LEFT = 0;
    private int RIGHT = 1;
    private int CENTER = 2;

    private int drawText(String text, int y, Graphics2D g, Font font, double pageWidth, int possition) {
        int textHeight = 0;
        int textWidth = 0;
        FontMetrics metrics = g.getFontMetrics(font);
        g.setFont(font);
        textHeight = metrics.getHeight();
        textWidth = metrics.stringWidth(text);

        int x = 0;
        switch (possition) {
            case 0://LEFT
                x = 0;
                break;
            case 1://RIGHT
                x = (int) (pageWidth - textWidth);
                break;
            case 2://CENTER
                x = (int) ((pageWidth - textWidth) / 2);
                break;
            default:
                x = 0;
                break;
        }
        if (x < 0) {
            x = 0;
        }

        textHeight = metrics.getHeight();
        g.drawString(text, x, y);
        return textHeight;
    }

    public Document convertToDocument() {
        Document obj = new Document();
        obj.append(KEY_ID, getId());
        obj.append(KEY_CUSTOMER_NAME, getCustomerName());
        obj.append(KEY_CUSTOMER_PHONE_NUMBER, getCustomerPhoneNumber());
        obj.append(KEY_CONTENT, getContent());
        obj.append(KEY_TOTAL_PRICE_NUM, getTotalPriceNum());
        obj.append(KEY_TOTAL_PRICE_STR, getTotalPriceStr());
        return obj;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @return the customerPhoneNumber
     */
    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @return the totalPriceNum
     */
    public String getTotalPriceNum() {
        return totalPriceNum;
    }

    /**
     * @return the totalPriceStr
     */
    public String getTotalPriceStr() {
        return totalPriceStr;
    }
}
