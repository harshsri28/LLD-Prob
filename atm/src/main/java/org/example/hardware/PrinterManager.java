package org.example.hardware;

import org.example.models.PrintJob;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PrinterManager {
    private final BlockingQueue<PrintJob> printQueue = new LinkedBlockingQueue<>();
    private volatile boolean isRunning = true;
    private Thread printerThread;

    public PrinterManager() {
        printerThread = new Thread(this::processPrintQueue, "PrinterThread");
        printerThread.setDaemon(true);
        printerThread.start();
    }

    public void printReceipt(String receiptContent) {
        PrintJob job = new PrintJob(receiptContent, Thread.currentThread().getName());
        try {
            printQueue.put(job); // Thread-safe enqueue
            System.out.println("[PRINTER] Print job queued by " + job.getRequester());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[PRINTER] Failed to queue print job.");
        }
    }

    private void processPrintQueue() {
        while (isRunning) {
            try {
                PrintJob job = printQueue.take(); // Blocks until job available
                System.out.println("[PRINTER] Printing for " + job.getRequester() + ": " + job.getContent());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void shutdown() {
        isRunning = false;
        printerThread.interrupt();
    }

    public int getPendingJobs() {
        return printQueue.size();
    }
}
