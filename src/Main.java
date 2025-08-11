import exception.FileProcessingException;
import service.MultiThreadedProcessor;
import service.ProducerConsumerProcessor;
import service.SingleThreadedProcessor;
import util.SimpleFileReader;
import util.TestFileGenerator;

public class Main {
    public static void main(String[] args) throws FileProcessingException {
        // Test dosyaları oluştur
        TestFileGenerator.generateTestFile("test_small.txt", 1);
        TestFileGenerator.generateTestFile("test_large.txt", 50);

        // Testleri çalıştır
        runComparison();
    }

    private static void runComparison() throws FileProcessingException {
        String filename = "test_large.txt";

        System.out.println("=== SINGLE THREADED ===");
        SingleThreadedProcessor singleProcessor = new SingleThreadedProcessor();
        singleProcessor.processFile(filename);
        System.out.println("-------------------------------------------------------------------------------------------");
        System.out.println("\n=== MULTI THREADED (4 threads) ===");
        MultiThreadedProcessor multiProcessor = new MultiThreadedProcessor(4);
        multiProcessor.processFile(filename);
        System.out.println("-------------------------------------------------------------------------------------------");
        System.out.println("\n=== PRODUCER-CONSUMER ===");
        // DÜZELTME: Constructor 3 parametre almalı + syntax error düzeltildi
        ProducerConsumerProcessor pcProcessor = new ProducerConsumerProcessor(100, 4, 1000);
        pcProcessor.processFile(filename);
    }
}
