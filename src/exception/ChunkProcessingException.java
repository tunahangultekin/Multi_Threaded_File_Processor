package exception;

public class ChunkProcessingException extends Exception {
    public ChunkProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}