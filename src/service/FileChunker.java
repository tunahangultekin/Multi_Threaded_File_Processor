package service;

import model.FileChunk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileChunker {

    private final int chunkSize;

    public FileChunker(int chunkSize) {
        this.chunkSize = Math.max(chunkSize, 1000); // Minimum 1000 lines
    }

    public List<FileChunk<String>> createChunks(String filename) throws IOException {

        Path path = Path.of(filename);
        List<String> allLines = Files.readAllLines(path);
        int totalLines = allLines.size();

        List<FileChunk<String>> chunks = new ArrayList<>();
        int chunkId = 1;


        for (int i = 0; i < totalLines; i += chunkSize) {
            int endIndex = Math.min(i + chunkSize, totalLines);
            List<String> chunkLines = new ArrayList<>(allLines.subList(i, endIndex));

            String chunkData = String.join("\n", chunkLines);// Parçadaki satırları tek bir string haline getirir.

            long startPos = i;
            long endPos = endIndex;

            chunks.add(createChunk(chunkId, chunkData, startPos, endPos));
            chunkId++;
        }


        return chunks;
    }


    private FileChunk<String> createChunk(int id, String data, long start, long end) {
        return new FileChunk<>(id, data, start, end);
    }
}
