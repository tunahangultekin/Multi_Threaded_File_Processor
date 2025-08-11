package util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class TestFileGenerator {

    private static final String[] WORDS ={
        "Java","Programming","Language","Thread","Test","Project","Practice","Ankara","Istanbul"
    };

    public static void generateTestFile(String filename,int sizeInMB){
        System.out.println("To generate a test file started.");


        File file=new File(filename);
        try {
            if (file.createNewFile()){
                System.out.println("File Created: "+file.getName());
            }


        }catch (IOException e){
            e.printStackTrace();
        }

        long targetSizeBytes=sizeInMB * 1024 * 1024;
        Random random=new Random();

        try (BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(filename))){
            long written=0;

            while (written<targetSizeBytes){
                int lineWordCount=10+random.nextInt(6);// Her satırda 10-15 kelime olmalı.

                StringBuilder line=new StringBuilder();

                for (int i = 0; i < lineWordCount; i++) {
                    String word=WORDS[random.nextInt(WORDS.length)];
                    line.append(word);

                    if (i<lineWordCount-1){// Kelimeler arasına boşluk ekle,son kelime hariç.
                        line.append(" ");
                    }

                }
                line.append("\n");

                byte[] data=line.toString().getBytes(StandardCharsets.UTF_8);

                //
                out.write(data);
                written+= data.length;

            }

            /*

            Biz StringBuilder ile satırı metin (String) olarak oluşturuyoruz.
            Ama dosyaya yazmak için bu metni byte dizisine çevirmemiz gerekiyor.
            getBytes(StandardCharsets.UTF_8) → String’i UTF-8 kodlamasına göre byte dizisine dönüştürür.
             */


        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("To generate a test file ended.");


    }



}
