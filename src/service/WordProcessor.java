package service;

import model.FileChunk;
import model.ProcessingResult;

import java.util.HashMap;
import java.util.Map;

public class WordProcessor {

    public ProcessingResult<Integer> processChunk(FileChunk<String> chunk) {
        long startTime = System.currentTimeMillis();

        try {

            String data = chunk.getData();

            // Chunk'taki kelimeleri say
            Map<String, Integer> wordCount = countWords(data);

            long endTime = System.currentTimeMillis();

            return new ProcessingResult<>(
                    chunk.getChunkId(),
                    wordCount,
                    endTime - startTime
            );

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            System.err.println("Error processing chunk " + chunk.getChunkId() + ": " + e.getMessage());
            return new ProcessingResult<>(
                    chunk.getChunkId(),
                    endTime - startTime,
                    e.getMessage()
            );
        }
    }

    private Map<String, Integer> countWords(String data) {
        Map<String, Integer> wordCount = new HashMap<>();

        // Null veya boş kontrolü
        if (data == null || data.isBlank()) {
            System.out.println("WARNING: Empty or null data received");
            return wordCount;
        }



        // Noktalama işaretlerini temizleyip kelimelere ayır
        String[] words = data
                .replaceAll("[^a-zA-Z0-9ğüşöçıİĞÜŞÖÇ\\s]", " ")  // DÜZELTME: \\s eklendi
                .toLowerCase()
                .split("\\s+");


        int validWords = 0;
        for (String word : words) {
            if (!word.isEmpty()) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                validWords++;
            }
        }


        return wordCount;
    }
}
