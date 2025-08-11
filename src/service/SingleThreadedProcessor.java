package service;

import model.FileChunk;
import model.ProcessingResult;
import util.PerformanceMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SingleThreadedProcessor {
    private final FileChunker chunker;
    private final WordProcessor processor;
    private final PerformanceMetrics metrics;

    public SingleThreadedProcessor() {
        this.chunker = new FileChunker(1000); // 1000 lines per chunk
        this.processor = new WordProcessor();
        this.metrics = new PerformanceMetrics();
    }

    public void processFile(String filename) {
        try {
            metrics.startMeasurement(filename, 1); // 1 thread

            // Dosyayı chunk'lara böl
            List<FileChunk<String>> chunks = chunker.createChunks(filename);
            System.out.println("Toplam chunk sayısı: " + chunks.size());

            // Her chunk'ı sırayla işle
            List<ProcessingResult<Integer>> results = new ArrayList<>();
            for (FileChunk chunk : chunks) {
                ProcessingResult<Integer> result = processor.processChunk(chunk);
                results.add(result);
            }

            //Sonuçları birleştir
            Map<String, Integer> finalResult = mergeResults(results);
            System.out.println("Toplam farklı kelime sayısı: " + finalResult.size());

            // Metrics bitir ve rapor yazdır
            metrics.endMeasurement();
            metrics.printReport();

        } catch (Exception e) {
            e.printStackTrace();
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