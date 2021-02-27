/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vietd
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;

import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

public class PrintText {

    public static void main(String[] args) throws PrintException, IOException {

        String defaultPrinter
                = PrintServiceLookup.lookupDefaultPrintService().getName();
        System.out.println("Default printer: " + defaultPrinter);
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        DocPrintJob job = service.createPrintJob();
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        pras.add(new Copies(1));
        printText("nguyen viet duc 1", pras, job);
//        printText("nguyen viet duc 2", pras, job);
//        printText("nguyen viet duc 3", pras, job);
//        printText("nguyen viet duc 4", pras, job);
//        printText("nguyen viet duc 5", pras, job);

        // prints the famous hello world! plus a form feed
//        InputStream is = new ByteArrayInputStream("hello world!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\f".getBytes("UTF8"));
//
//        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
//        pras.add(new Copies(1));
//
//        Doc doc = new SimpleDoc(is, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
//        DocPrintJob job = service.createPrintJob();
//
//        PrintJobWatcher pjw = new PrintJobWatcher(job);
//        job.print(doc, pras);
//        pjw.waitForDone();
//        is.close();
    }

    private static void printText(String text, PrintRequestAttributeSet pras, DocPrintJob job) throws PrintException, UnsupportedEncodingException, IOException {

        String input = text + "\n";
        InputStream is = new ByteArrayInputStream(input.getBytes("UTF8"));
        Doc doc = new SimpleDoc(is, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
        job.print(doc, pras);
        PrintJobWatcher pjw = new PrintJobWatcher(job);
        pjw.waitForDone();
        is.close();
    }
}

class PrintJobWatcher {

    boolean done = false;

    PrintJobWatcher(DocPrintJob job) {
        job.addPrintJobListener(new PrintJobAdapter() {
            public void printJobCanceled(PrintJobEvent pje) {
                System.out.println("Printing done printJobCanceled...");
                allDone();
            }

            public void printJobCompleted(PrintJobEvent pje) {
                System.out.println("Printing done printJobCompleted...");
                allDone();
            }

            public void printJobFailed(PrintJobEvent pje) {
                System.out.println("Printing done printJobFailed...");
                allDone();
            }

            public void printJobNoMoreEvents(PrintJobEvent pje) {
                System.out.println("Printing done printJobNoMoreEvents...");
                allDone();
            }

            void allDone() {
                synchronized (PrintJobWatcher.this) {
                    done = true;
                    System.out.println("Printing done ...");
                    PrintJobWatcher.this.notify();
                }
            }
        });
    }

    public synchronized void waitForDone() {
        try {
            while (!done) {
                wait();
            }
        } catch (InterruptedException e) {
        }
    }
}
