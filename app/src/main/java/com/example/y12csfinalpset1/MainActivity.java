package com.example.y12csfinalpset1;

import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import com.google.android.material.textfield.TextInputLayout;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private TextView outputView;
    private TextView uniqueWordsView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //implement
        setContentView(R.layout.activity_main);

        outputView = findViewById(R.id.outputView);
        uniqueWordsView = findViewById(R.id.uniqueWordsView);

        Intent intent = getIntent();
        String fileName = intent.getStringExtra("fileName");

        /*try { */
            if (fileName != null) {
                String textContent = readTextOrPdfFile(fileName);
                String commonWordsContent = readTextFileFromAssets("commonWords.txt");

                List<String> commonWords = Arrays.asList(commonWordsContent.split("\\s+"));
                Set<String> commonWordSet = new HashSet<>(commonWords);

                analyzeText(textContent, commonWordSet);
            } else {
                outputView.setText("No file selected.");
            }
        /* } catch (IOException e) {
            outputView.setText("Error reading file: " + e.getMessage());
        }*/



        TextView paragraphView = findViewById(R.id.paragraphView);
        TextView temperatureDisplay = findViewById(R.id.temperatureDisplay);
        TextView temperatureExplanation = findViewById(R.id.temperatureExplanation);

        String textContent = readTextOrPdfFile(fileName);
        textContent = textContent.toLowerCase().replaceAll("[^a-zA-Z0-9.\'\\s]", "");
        List<String> words = Arrays.asList(textContent.split("\\s+"));

        Random random = new Random();
        double temperature = 0.3 + (0.7 * random.nextDouble()); // Random temperature between 0.3 and 1.0


        String randomParagraph = generateRandomParagraph(words, 250, temperature); // 250 words in the paragraph
        String explanation = getTemperatureExplanation(temperature);

        temperatureDisplay.setText(String.format("Generated Temperature: %.2f", temperature));
        temperatureExplanation.setText(explanation);
        paragraphView.setText(randomParagraph);

    }




    public String readTextOrPdfFile(String fileName) {
        if (fileName.toLowerCase().endsWith(".txt")) {
            return readTextFileFromAssets(fileName);
        } else if (fileName.toLowerCase().endsWith(".pdf")) {
            return readPdfFileFromAssets(fileName);
        } else {
            return "Unsupported file format.";
        }
    }

    private String readTextFileFromAssets(String fileName) {
        AssetManager assetManager = getAssets();
        StringBuilder content = new StringBuilder();

        try (InputStream inputStream = assetManager.open(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            return "Error reading text file: " + e.getMessage();
        }

        return content.toString();
    }

    private String readPdfFileFromAssets(String fileName) {
        AssetManager assetManager = getAssets();
        StringBuilder content = new StringBuilder();

        try (InputStream inputStream = assetManager.open(fileName)){
            PDDocument document = new PDDocument();
            document.load(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            content.append(stripper.getText(document));
            document.close();

        } catch (IOException e) {
            return "Error reading PDF file: " + e.getMessage();
        }

        return content.toString();
    }


    private void analyzeText(String textContent, Set<String> commonWordSet) {
        // Normalize text
        textContent = textContent.toLowerCase().replaceAll("[^a-zA-Z0-9.\'\\s]", "");
        //Normalize text in commonWordSet


        // Word and sentence counts
        String[] words = textContent.split("\\s+");
        int wordCount = words.length;

        String[] sentences = textContent.split("[.!?]\\s*");
        int sentenceCount = sentences.length;

        // Unique words
        Set<String> uniqueWords = Arrays.stream(words)
                .filter(word -> !commonWordSet.contains(word))
                .collect(Collectors.toSet());
        int uniqueWordCount = uniqueWords.size();

        // Word frequencies
        Map<String, Integer> wordFrequencies = new HashMap<>();
        for (String word : words) {
            if (!commonWordSet.contains(word)) {
                wordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0) + 1);
            }
        }

        // Top 5 words
        /* List<Map.Entry<String, Integer>> topFiveWords = wordFrequencies.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .collect(Collectors.toList()); */

        List<Map.Entry<String, Integer>> topFiveWords = wordFrequencies.entrySet().stream()
                .sorted((e1, e2) -> {
                    int freqCompare = e2.getValue().compareTo(e1.getValue());
                    return (freqCompare != 0) ? freqCompare : e1.getKey().compareTo(e2.getKey());
                })
                .limit(5)
                .collect(Collectors.toList());


        // Build output
        StringBuilder result = new StringBuilder();
        result.append("\n Total Words: ").append(wordCount).append("\n \n");
        result.append("Total Sentences: ").append(sentenceCount).append("\n \n");
        /* result.append("Unique Words: ").append(uniqueWordCount).append("\n \n");
        result.append("Unique Word List: ").append(uniqueWords).append("\n \n"); */
        result.append("Top 5 Words: \n");
        for (Map.Entry<String, Integer> entry : topFiveWords) {
            result.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        outputView.setText(result.toString());

        uniqueWordsView.setText("Unique Word List:\n" + String.join(" , ", uniqueWords));

    }


    /*
    private String generateRandomParagraph(Map<String, Integer> wordFrequencies, int wordCount, double temperature) {
        // Normalize word frequencies to probabilities
        double totalFrequency = wordFrequencies.values().stream().mapToDouble(f -> Math.pow(f, 1 / temperature)).sum();
        Map<String, Double> probabilities = wordFrequencies.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Math.pow(entry.getValue(), 1 / temperature) / totalFrequency
                ));

        // Create a weighted random selection
        List<String> words = new ArrayList<>(probabilities.keySet());
        List<Double> cumulativeProbabilities = new ArrayList<>();
        double cumulative = 0.0;
        for (String word : words) {
            cumulative += probabilities.get(word);
            cumulativeProbabilities.add(cumulative);
        }

        // Generate the paragraph
        Random random = new Random();
        StringBuilder paragraph = new StringBuilder();
        for (int i = 0; i < wordCount; i++) {
            double r = random.nextDouble();
            for (int j = 0; j < cumulativeProbabilities.size(); j++) {
                if (r <= cumulativeProbabilities.get(j)) {
                    paragraph.append(words.get(j));

                    // Randomly decide if a comma or period should follow the word

                    // Randomly decide if a comma or period should follow the word
                    double punctuationChance = random.nextDouble();
                    if (punctuationChance < 0.25 && punctuationChance > 0.15) {
                        paragraph.append(", ");
                    } else if (punctuationChance < 0.15) {
                        paragraph.append(". ");
                    } else {
                        paragraph.append(" ");
                    }
                    break;
                }
            }
        }



        paragraph.append(".");
        return paragraph.toString().trim();
    } */

    private String getTemperatureExplanation(double temperature) {
        if (temperature >= 0.75) {
            return "High temperature (>= 0.75): More randomness; frequent and rare words are equally likely.";
        } else if (temperature >= 0.5) {
            return "Medium temperature (0.5 - 0.75): Balanced randomness; less frequent words are included occasionally.";
        } else {
            return "Low temperature (< 0.5): Less randomness; common words dominate the paragraph.";
        }
    }

    private String generateRandomParagraph(List<String> words, int wordCount, double temperature) {
        Random random = new Random();
        StringBuilder paragraph = new StringBuilder();

        for (int i = 0; i < wordCount; i++) {
            String randomWord = words.get(random.nextInt(words.size()));

            // With temperature: Skip some words to simulate rare word usage
            if (temperature < 1.0 && random.nextDouble() > temperature) {
                continue; // Skip this word
            }

            paragraph.append(randomWord);
            double punctuationChance = random.nextDouble();
            if (punctuationChance < 0.20 && punctuationChance > 0.05) {
                paragraph.append(", ");
            } else if (punctuationChance < 0.05) {
                paragraph.append(". ");
            } else {
                paragraph.append(" ");
            }
        }

        paragraph.append(".");
        return paragraph.toString().trim();
    }




}






