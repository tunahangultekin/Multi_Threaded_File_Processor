package model;

public class FileChunk<T> {
    private final int chunkId;           // Hangi chunk? (1,2,3...)
    private final T data;                // İçeriği ne? (String, byte[], vs)
    private final long startPosition;    // Dosyada nereden başlıyor?
    private final long endPosition;      // Dosyada nerede bitiyor?

    // Constructor
    public FileChunk(int chunkId, T data, long startPosition, long endPosition) {
        this.chunkId = chunkId;
        this.data = data;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    // Getter'lar
    public int getChunkId() { return chunkId; }
    public T getData() { return data; }
    public long getStartPosition() { return startPosition; }
    public long getEndPosition() { return endPosition; }
    public long getSize() { return endPosition - startPosition; }

    @Override
    public String toString() {
        return String.format("FileChunk{id=%d, size=%d bytes, pos=%d-%d}",
                chunkId, getSize(), startPosition, endPosition);
    }
}