package util;

import exception.FileProcessingException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleFileReader {


    public List<String> readFileLines(String filename) throws IOException, FileProcessingException {

        //TODO DOSYAYI SATIR SATIR OKU

        List<String> lines=new ArrayList<>();

        try (BufferedReader bufferedReader=new BufferedReader(new FileReader(filename))){
            String line;
            while((line=bufferedReader.readLine())!=null){
                lines.add(line);
            }

        }catch (IOException e){
            // IOException'ı FileProcessingException ile wrap edip fırlatıyoruz
            throw new FileProcessingException("Dosya okunurken hata oluştu: " + filename, e);
        }
        


        return lines;
    }

    public int countWords(List<String> lines){
        //TODO TOPLAM KELİME SAYISINI BUL
        
        int totalWords=0;

        for (String line : lines) {
            if (line==null || line.isEmpty()){
                continue;
            }
            //Separate the line from spaces.
            String[] words=line.trim().split("\\s+");
            totalWords+=words.length;

        }
        return totalWords;

    }

    public void processFile(String filename) throws FileProcessingException {

        try {
            long startTime=System.currentTimeMillis();

            List<String> lines=readFileLines(filename);
            int wordCount=countWords(lines);

            long endTime=System.currentTimeMillis();

            System.out.println("File: " + filename);
            System.out.println("Lines: " + lines.size());
            System.out.println("Words: " + wordCount);
            System.out.println("Time: " + (endTime - startTime) + " ms");


        }catch (IOException e){
            // IOException'ı FileProcessingException ile wrap edip fırlatıyoruz
            throw new FileProcessingException("Dosya okunurken hata oluştu: " + filename, e);
        }


    }

}
