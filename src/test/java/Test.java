
import static com.oracle.jrockit.jfr.ContentType.Timestamp;
import com.vdbk.apps.quanlybanhang.database.DatabaseManager;
import com.vdbk.apps.quanlybanhang.database.Item;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import static java.awt.print.PageFormat.LANDSCAPE;
import static java.awt.print.PageFormat.PORTRAIT;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author vietd
 */
public class Test {

    public static void main(String args[]) throws UnknownHostException {
        float number = 7.5f;
        int a = 2;
        int b = (int) (number / a);
        float c = number - b * a;
       
        System.out.println("start print");
    //    printToPrinter();
    
        String test = "abc_123";
        String test2 = "abc";
       
        System.out.println("end print"+test.split("_")[0]);
        System.out.println("end print"+test2.split("_")[0]);
        
        long id = System.currentTimeMillis();
        String timeStamp = new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Timestamp(id));
        System.out.print(timeStamp);
        
    }

    public static String fmt(float d) {
        if (d == (long) d) {
            return String.format("%d", (long) d);
        } else {
            return String.format("%s", d);
        }
    }

    private static void printToPrinter() {
        String printData = "nguyen viet duc" + "\n" + "le thi van anh";
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat format = new PageFormat();
        job.validatePage(format);
        
        job.setPrintable(new OutputPrinter(printData),format);
        boolean doPrint = job.printDialog();
       

        if (doPrint) {
            try {
                job.print();

            } catch (PrinterException e) {
                e.printStackTrace();
                // Print job did not complete.
            }
        }
    }

    public static class OutputPrinter implements Printable {

        private String printData;

        public OutputPrinter(String printDataIn) {
            this.printData = printDataIn;
        }

        @Override
        public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
            // Should only have one page, and page # is zero-based.
            if (page > 0) {
                System.out.println("NO SUCH PAGE");
                return NO_SUCH_PAGE;
            }
          
            System.out.println("printing"+pf.getOrientation()+" "+pf.getWidth()+"x"+pf.getHeight());
            // Adding the "Imageable" to the x and y puts the margins on the page.
            // To make it safe for printing.
            Graphics2D g2d = (Graphics2D) g;
            int x = (int)pf.getImageableWidth();
            int y = (int)pf.getImageableHeight();
        //    g2d.translate(x, y);
            System.out.println("Graphic 2D: " + x + ":" + y);
            
            // Calculate the line height
            Font font = new Font("Serif", Font.PLAIN, 10);
            FontMetrics metrics = g.getFontMetrics(font);
            int lineHeight = metrics.getHeight();
            
            BufferedReader br = new BufferedReader(new StringReader(printData));

            // Draw the page:
            
                String line;

                // Just a safety net in case no margin was added.
              //  x += 50;
              //  y += 50;
                for(int i = 0;i< 10;i++){
                    y += lineHeight;
                    System.out.println("print line: " + x);
                   g2d.drawString("nguyen viet duc", 0, y);
                }
                
//                while ((line = br.readLine()) != null) {
//                    System.out.println("print line: " + line);
//                    x += lineHeight;
//                    g2d.drawString(line, y, x);
//                }
            

            return PAGE_EXISTS;
        }
    }
}
