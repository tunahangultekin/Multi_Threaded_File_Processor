package model;

import java.util.HashMap;
import java.util.Map;

public class ProcessingResult<T> {
    private final int chunkId;                    // Hangi chunk'ın sonucu?
    private final Map<String, T> results;         // Ne bulduk? (kelime:count map)
    private final long processingTime;            // Kaç ms sürdü?
    private final boolean successful;             // Başarılı mı?
    private final String errorMessage;            // Hata varsa ne?

    // Başarılı sonuç için constructor
    public ProcessingResult(int chunkId, Map<String, T> results, long processingTime) {
        this.chunkId = chunkId;
        this.results = results;
        this.processingTime = processingTime;
        this.successful = true;
        this.errorMessage = null;
    }

    // Hatalı sonuç için constructor
    public ProcessingResult(int chunkId, long processingTime, String errorMessage) {
        this.chunkId = chunkId;
        this.results = new HashMap<>();
        this.processingTime = processingTime;
        this.successful = false;
        this.errorMessage = errorMessage;
    }

    // Getter'lar
    public int getChunkId() { return chunkId; }
    public Map<String, T> getResults() { return results; }
    public long getProcessingTime() { return processingTime; }
    public boolean isSuccessful() { return successful; }
    public String getErrorMessage() { return errorMessage; }

    @Override
    public String toString() {
        if (successful) {
            return String.format("ProcessingResult{chunkId=%d, resultCount=%d, time=%dms}",
                    chunkId, results.size(), processingTime);
        } else {
            return String.format("ProcessingResult{chunkId=%d, ERROR='%s', time=%dms}",
                    chunkId, errorMessage, processingTime);
        }
    }
}