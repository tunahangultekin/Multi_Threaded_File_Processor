package service;


import model.FileChunk;
import model.ProcessingResult;
import util.PerformanceMetrics;

import java.util.concurrent.*;
import java.util.*;

public class MultiThreadedProcessor {
    private final FileChunker chunker;
    private final WordProcessor processor;
    private final PerformanceMetrics metrics;
    private final int threadPoolSize;

    public MultiThreadedProcessor(int threadPoolSize) {
        this.chunker = new FileChunker(1000);
        this.processor = new WordProcessor();
        this.metrics = new PerformanceMetrics();
        this.threadPoolSize = threadPoolSize;
    }

    public void processFile(String filename) {
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        try {
            metrics.startMeasurement(filename, threadPoolSize);

            // Dosyayı chunk'lara böl
            List<FileChunk<String>> chunks = chunker.createChunks(filename);
            System.out.println("Toplam chunk sayısı: " + chunks.size());

            // Callable görev listesi oluştur
            List<Callable<ProcessingResult<Integer>>> tasks = new ArrayList<>();
            for (FileChunk chunk : chunks) {
                tasks.add(() -> processor.processChunk(chunk));
            }

            // Tüm görevleri çalıştır
            List<Future<ProcessingResult<Integer>>> futures = executor.invokeAll(tasks);

            // Future sonuçlarını topla
            List<ProcessingResult<Integer>> results = new ArrayList<>();
            for (Future<ProcessingResult<Integer>> future : futures) {
                results.add(future.get());
            }

            // Sonuçları birleştir
            Map<String, Integer> finalResult = mergeResults(results);
            System.out.println("Toplam farklı kelime sayısı: " + finalResult.size());

            // Metrics bitir ve rapor yazdır
            metrics.endMeasurement();
            metrics.printReport();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private Map<String, Integer> mergeResults(List<ProcessingResult<Integer>> results) {
        Map<String, Integer> merged = new HashMap<>();

        for (ProcessingResult<Integer> result : results) {
            Map<String, Integer> map = result.getResults();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                merged.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        return merged;
    }
}
