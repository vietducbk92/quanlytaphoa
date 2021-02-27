
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.print.*;

public class HelloWorldPrinter implements Printable, ActionListener {

    public int print(Graphics g, PageFormat pf, int page) throws
            PrinterException {

        if (page > 0) {
            /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }
        int x = 0;
        int y = 50;
        int lineHeight = 0;
                
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        
        //draw HEADER
        Font font = new Font("Serif", Font.BOLD, 15);
        FontMetrics metrics = g.getFontMetrics(font);
        g.setFont(font);
        lineHeight = metrics.getHeight();
        g.drawString("TẠP HÓA LÝ HOÀN", x, y);
        y+=lineHeight;
        g.drawString("-------------------------------------",x,y);
        y+=lineHeight;
        g.drawString("HÓA ĐƠN BÁN HÀNG", x, y);
        y+=lineHeight;
        
        //draw bill
        font = new Font("Serif", Font.PLAIN, 10);
        metrics = g.getFontMetrics(font);
        g.setFont(font);
        lineHeight = metrics.getHeight();
        g.drawString("KEM ĐÁNH RĂNG", x, y);
        y+=lineHeight;
        g.drawString("SL: "+1 +"T.TIỀN:"+100000, x, y);
        y+=lineHeight;
        g.drawString("KEM ĐÁNH RĂNG 2", x, y);
        y+=lineHeight;
        g.drawString("SL: "+1 +"T.TIỀN:"+1000000, x, y);
        y+=lineHeight;
        font = new Font("Serif", Font.BOLD, 10);
        g.setFont(font);
        g.drawString("TỔNG: "+1000000, x, y);
        y+=lineHeight;
        font = new Font("Serif", Font.ITALIC, 10);
        g.setFont(font);
        g.drawString("Cảm ơn quý khách",x,y);
        
        

//        Font font = new Font("Serif", Font.PLAIN, 10);
//        FontMetrics metrics = g.getFontMetrics(font);
//        g.setFont(font);
//        int lineHeight = metrics.getHeight();
//        
//        /* Now we perform our rendering */
//      
//        for(int i = 0;i<5;i++){
//            g.drawString("Xin chao LE THI VAN ANH "+i, x, y);
//            y+=lineHeight;
//        }

        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }

    public void actionPerformed(ActionEvent e) {
        PrinterJob job = PrinterJob.getPrinterJob();
        
        PageFormat format = new PageFormat();
        job.validatePage(format);
        job.setPrintable(this,format);
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

    public static void main(String args[]) {

        UIManager.put("swing.boldMetal", Boolean.FALSE);
        JFrame f = new JFrame("Hello World Printer");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        JButton printButton = new JButton("Print Hello World");
        printButton.addActionListener(new HelloWorldPrinter());
        f.add("Center", printButton);
        f.pack();
        f.setVisible(true);
    }
}
