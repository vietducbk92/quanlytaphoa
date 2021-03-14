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

/**
 *
 * @author vietd
 */
public class Bill implements Printable {

    private long id;
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

    public Bill(String customerName, String phoneNumber, ArrayList<BillItem> billItems, String totalPriceNum, String totalPriceStr) {
        id = System.currentTimeMillis();
        this.customerName = customerName;
        this.customerPhoneNumber = phoneNumber;
        this.totalPriceNum = totalPriceNum;
        this.totalPriceStr = totalPriceStr;
        this.content = "";
        for (BillItem item : billItems) {
            this.content = this.content + item.getName() + "-" + Utils.fmt(item.getUnitPrice()) + "-" + Utils.fmt(item.getNumber()) + "-" + item.getUnit() + "-" + Utils.fmt(item.getTotalPrice()) + "/";
        }
    }

    public void print() {
        PrinterJob job = PrinterJob.getPrinterJob();

        PageFormat format = new PageFormat();
        job.validatePage(format);
        job.setPrintable(this, format);
        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
            } catch (PrinterException ex) {
                ex.printStackTrace();
                /* The job did not successfully complete */
            }
        }
    }

    public int print(Graphics g, PageFormat pf, int page) throws
            PrinterException {

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

        System.out.println("pf.get WxH: " + pf.getWidth() + "x" + pf.getHeight());
        System.out.println("pf.get1 WxH: " + pf.getImageableWidth() + "x" + pf.getImageableHeight());
        System.out.println("pf.get2 WxH: " + pf.getImageableX() + "x" + pf.getImageableY());

        int pageWidth = (int) pf.getImageableY() * 2 - padding;
        //draw HEADER
        //store name
        Font font = new Font("Serif", Font.BOLD, 9);
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
        String timeStamp = new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Timestamp(id));
        lineHeight = drawText(timeStamp, y, g2d, font, pageWidth, CENTER);
        y += lineHeight;
        //draw customer name
        String text = customerName.isEmpty() ? "Khách lẻ" : customerName;
        lineHeight = drawText("Khách hàng: " + text, y, g2d, font, pageWidth, LEFT);
        y += lineHeight;
        //draw customer phone
        if (!customerPhoneNumber.isEmpty()) {
            lineHeight = drawText("SDT: " + customerPhoneNumber, y, g2d, font, pageWidth, LEFT);
            y += lineHeight;
        }
        //draw content
        g.drawLine(0, y, pageWidth, y);
        y += lineHeight;

        String[] items = content.split("/");
        for (String item : items) {
            String[] itemDetail = item.split("-");
            String name = itemDetail[0].toLowerCase();
            String unitPrice = itemDetail[1];
            String number = itemDetail[2];
            
            String unit = itemDetail[3].toLowerCase();
            String price = itemDetail[4];

            lineHeight = drawText(name, y, g2d, font, pageWidth, LEFT);
            y += lineHeight;
            lineHeight = drawText(unitPrice+"x"+ number + " " + unit, y, g2d, font, pageWidth, LEFT);
            lineHeight = drawText(price, y, g2d, font, pageWidth, RIGHT);
            y += lineHeight;
        }
        g.drawLine(0, y, pageWidth, y);
        y += lineHeight;
        //Draw total price
        font = new Font("Serif", Font.BOLD, 9);
        lineHeight = drawText("Tổng:", y, g2d, font, pageWidth, LEFT);
        lineHeight = drawText(totalPriceNum, y, g2d, font, pageWidth, RIGHT);
        y += lineHeight;
        font = new Font("Serif", Font.ITALIC, 9);
        lineHeight = drawText(totalPriceStr, y, g2d, font, pageWidth, LEFT);
        y += lineHeight * 2;

        drawText("Cảm ơn quý khách", y, g2d, font, pageWidth, CENTER);
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
}
