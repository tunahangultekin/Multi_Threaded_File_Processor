package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;

public class PerformanceMetrics {

    private long startTime;
    private long endTime;
    private long memoryBefore;
    private long memoryAfter;
    private int threadCount;
    private String fileName;

    public void startMeasurement(String fileName, int threadCount) {
        this.fileName = fileName;
        this.threadCount = threadCount;
        // zaman ve bellek kaydı
        this.startTime = System.currentTimeMillis();
        this.memoryBefore = getUsedMemory();
    }

    public void endMeasurement() {
        // bitiş zamanı ve final bellek
        this.endTime = System.currentTimeMillis();
        this.memoryAfter = getUsedMemory();
    }
    public void printReport() {
        if (startTime == 0) {
            System.out.println("[PerformanceMetrics] startMeasurement() çağrılmamış.");
            return;
        }
        if (endTime == 0) {
            System.out.println("[PerformanceMetrics] endMeasurement() çağrılmamış. printReport() önce endMeasurement() çağırın.");
            return;
        }

        long durationMs = endTime - startTime;
        double durationSec = durationMs / 1000.0;

        long memoryDiffBytes = memoryAfter - memoryBefore;
        double memoryBeforeMB = memoryBefore / (1024.0 * 1024.0);
        double memoryAfterMB  = memoryAfter  / (1024.0 * 1024.0);
        double memoryDiffMB   = memoryDiffBytes / (1024.0 * 1024.0);

        long fileSizeBytes = -1;
        try {
            if (fileName != null && !fileName.isEmpty()) {
                Path p = Path.of(fileName);
                if (Files.exists(p)) {
                    fileSizeBytes = Files.size(p);
                }
            }
        } catch (IOException e) {
            // dosya boyutu okunamadıysa -1 bırak
            fileSizeBytes = -1;
        }

        double throughputMBps = -1;
        if (durationSec > 0 && fileSizeBytes > 0) {
            double bytesPerSec = fileSizeBytes / durationSec;
            throughputMBps = bytesPerSec / (1024.0 * 1024.0);
        }

        DecimalFormat df = new DecimalFormat("#,##0.00");

        System.out.println("=== Performance Report ===");
        System.out.println("File: " + (fileName == null ? "-" : fileName));
        System.out.println("Threads: " + threadCount);
        System.out.printf("Processing time: %d ms (%.3f s)%n", durationMs, durationSec);
        System.out.printf("Memory before: %s MB, after: %s MB, delta: %s MB%n",
                df.format(memoryBeforeMB), df.format(memoryAfterMB), df.format(memoryDiffMB));
        if (fileSizeBytes >= 0) {
            System.out.printf("File size: %d bytes (%.2f MB)%n", fileSizeBytes, fileSizeBytes / (1024.0 * 1024.0));
        } else {
            System.out.println("File size: unavailable");
        }

        if (throughputMBps >= 0) {
            System.out.printf("Throughput: %s MB/s (total)%n", df.format(throughputMBps));
            if (threadCount > 0) {
                System.out.printf("Per-thread throughput: %s MB/s%n", df.format(throughputMBps / threadCount));
            }
        } else {
            System.out.println("Throughput: unavailable (süre 0 veya dosya boyutu bilinmiyor)");
        }

        System.out.println("===========================");
    }

    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }


}
