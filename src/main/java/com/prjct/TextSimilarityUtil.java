package com.prjct;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TextSimilarityUtil {
    public static double calculateSimilarity(String text1, String text2) {

        Map<String, Integer> freq1 = buildFrequencyMap(text1);
        Map<String, Integer> freq2 = buildFrequencyMap(text2);

        Set<String> words = new HashSet<>();
        words.addAll(freq1.keySet());
        words.addAll(freq2.keySet());

        double dot = 0, mag1 = 0, mag2 = 0;

        for (String word : words) {
            int v1 = freq1.getOrDefault(word, 0);
            int v2 = freq2.getOrDefault(word, 0);

            dot += v1 * v2;
            mag1 += v1 * v1;
            mag2 += v2 * v2;
        }

        if (mag1 == 0 || mag2 == 0) return 0;

        return (dot / (Math.sqrt(mag1) * Math.sqrt(mag2))) * 100;
    }

    private static Map<String, Integer> buildFrequencyMap(String text) {
        Map<String, Integer> map = new HashMap<>();

        String cleaned = text.toLowerCase().replaceAll("[^a-z ]", " ");
        String[] words = cleaned.split("\\s+");

        for (String word : words) {
            if (word.length() > 2) {
                map.put(word, map.getOrDefault(word, 0) + 1);
            }
        }
        return map;
}
}