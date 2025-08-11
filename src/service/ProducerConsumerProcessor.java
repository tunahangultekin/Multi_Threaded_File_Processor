package service;


import exception.FileProcessingException;
import model.FileChunk;
import model.ProcessingResult;
import util.PerformanceMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;


public class ProducerConsumerProcessor {

    // Configuration
    private final int queueSize;
    private final int consumerCount;
    private final int chunkSize;

    // Components
    private final BlockingQueue<FileChunk<String>> queue;
    private final List<ProcessingResult<Integer>> results;
    private final WordProcessor processor;
    private final PerformanceMetrics metrics;

    // Poison pill
    private static final FileChunk<String> POISON_PILL =
            new FileChunk<>(-1, "POISON", -1, -1);

    // Thread management
    private ExecutorService producerExecutor;
    private ExecutorService consumerExecutor;


    public ProducerConsumerProcessor(int queueSize, int consumerCount, int chunkSize) {

        if (queueSize <= 0) throw new IllegalArgumentException("Queue size must be > 0");
        if (consumerCount <= 0) throw new IllegalArgumentException("Consumer count must be > 0");
        if (chunkSize <= 0) throw new IllegalArgumentException("Chunk size must be > 0");

        this.queueSize = queueSize;
        this.consumerCount = consumerCount;
        this.chunkSize = chunkSize;

        // Thread-safe collections
        this.queue = new ArrayBlockingQueue<>(queueSize);
        this.results = Collections.synchronizedList(new ArrayList<>());

        // Components
        this.processor = new WordProcessor();
        this.metrics = new PerformanceMetrics();


    }

    public void processFile(String filename) throws FileProcessingException {


        // Performance monitoring başlat
        metrics.startMeasurement(filename, consumerCount);

        try {
            // Thread pools oluştur
            producerExecutor = Executors.newSingleThreadExecutor();
            consumerExecutor = Executors.newFixedThreadPool(consumerCount);

            // Producer'ı başlat
            Future<?> producerFuture = producerExecutor.submit(new Producer(filename));

            // Consumer'ları başlat
            List<Future<?>> consumerFutures = new ArrayList<>();
            for (int i = 0; i < consumerCount; i++) {
                Future<?> future = consumerExecutor.submit(new Consumer(i + 1));
                consumerFutures.add(future);
            }

            // Producer'ın bitmesini bekle
            producerFuture.get();


            // Consumer'ların bitmesini bekle
            for (Future<?> future : consumerFutures) {
                future.get();
            }


            // Sonuçları işle
            processResults();

        } catch (InterruptedException | ExecutionException e) {
            throw new FileProcessingException("Error in producer-consumer processing", e);
        } finally {
            shutdownExecutors();
            metrics.endMeasurement();
            metrics.printReport();
        }
    }


    // ================================
    // PRODUCER THREAD
    // ================================
    private class Producer implements Runnable {
        private final String filename;

        public Producer(String filename) {
            this.filename = filename;
        }

        @Override
        public void run() {


            try {

                List<String> allLines = Files.readAllLines(Path.of(filename));
                System.out.println("Producer read " + allLines.size() + " lines");

                // Chunk'lara böl ve queue'ya ekle
                int chunkId = 1;
                int totalChunks = (int) Math.ceil((double) allLines.size() / chunkSize);


                for (int i = 0; i < allLines.size(); i += chunkSize) {
                    // Chunk boundaries
                    int endIndex = Math.min(i + chunkSize, allLines.size());
                    List<String> chunkLines = allLines.subList(i, endIndex);

                    // String olarak birleştir
                    String chunkData = String.join("\n", chunkLines);

                    // FileChunk oluştur
                    FileChunk<String> chunk = new FileChunk<>(
                            chunkId++,
                            chunkData,
                            i,
                            endIndex
                    );

                    // Queue'ya ekle (blocking operation)
                    queue.put(chunk);

                }

                // Poison pills gönder (her consumer için bir tane)
                for (int i = 0; i < consumerCount; i++) {
                    queue.put(POISON_PILL);
                }


            } catch (IOException e) {
                System.err.println("Producer error reading file: " + e.getMessage());
            } catch (InterruptedException e) {
                System.err.println("Producer interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    // ================================
    // CONSUMER THREADS
    // ================================
    private class Consumer implements Runnable {
        private final int consumerId;

        public Consumer(int consumerId) {
            this.consumerId = consumerId;
        }

        @Override
        public void run() {


            int processedChunks = 0;

            try {
                while (true) {
                    // Queue'dan chunk al (blocking operation)
                    FileChunk<String> chunk = queue.take();

                    // Poison pill kontrolü
                    if (chunk == POISON_PILL) {

                        break;
                    }


                    ProcessingResult<Integer> result = processor.processChunk(chunk);

                    // Sonucu thread-safe list'e ekle
                    results.add(result);
                    processedChunks++;

                }

            } catch (InterruptedException e) {
                System.err.println("Consumer-" + consumerId + " interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
            }


        }
    }

    // ================================
    // RESULT PROCESSING
    // ================================
    private void processResults() {
        System.out.println("\n--- Processing Results ---");

        // Sonuçları chunk ID'ye göre sırala
        results.sort(Comparator.comparingInt(ProcessingResult::getChunkId));

        // İstatistikler
        int successfulChunks = 0;
        int failedChunks = 0;
        long totalProcessingTime = 0;
        Map<String, Integer> mergedWordCount = new HashMap<>();

        for (ProcessingResult<Integer> result : results) {
            if (result.isSuccessful()) {
                successfulChunks++;
                totalProcessingTime += result.getProcessingTime();

                // Kelime sayılarını merge et
                for (Map.Entry<String, Integer> entry : result.getResults().entrySet()) {
                    mergedWordCount.merge(entry.getKey(), entry.getValue(), Integer::sum);
                }
            } else {
                failedChunks++;
                System.err.println("Failed chunk " + result.getChunkId() +
                        ": " + result.getErrorMessage());
            }
        }

        // Rapor yazdır
        System.out.println("Total chunks processed: " + results.size());
        System.out.println("Successful chunks: " + successfulChunks);
        System.out.println("Failed chunks: " + failedChunks);
        System.out.println("Total processing time: " + totalProcessingTime + "ms");
        System.out.println("Average processing time per chunk: " +
                (totalProcessingTime / Math.max(successfulChunks, 1)) + "ms");

        // Top 10 words
        System.out.println("\n--- Top 10 Words ---");
        mergedWordCount.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(entry ->
                        System.out.println(entry.getKey() + ": " + entry.getValue()));

        System.out.println("Total unique words: " + mergedWordCount.size());
    }

    // ================================
    // CLEANUP
    // ================================
    private void shutdownExecutors() {
        if (producerExecutor != null) {
            producerExecutor.shutdown();
            try {
                if (!producerExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    producerExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                producerExecutor.shutdownNow();
            }
        }

        if (consumerExecutor != null) {
            consumerExecutor.shutdown();
            try {
                if (!consumerExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    consumerExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                consumerExecutor.shutdownNow();
            }
        }
    }

    // ================================

    // Getters
    public List<ProcessingResult<Integer>> getResults() {
        return new ArrayList<>(results);
    }

    public int getQueueSize() {
        return queueSize;
    }

    public int getConsumerCount() {
        return consumerCount;
    }
}
