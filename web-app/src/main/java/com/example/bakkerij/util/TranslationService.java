package com.example.bakkerij.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TranslationService {
    private final Map<String, Map<String, String>> translations = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public void loadTranslations(String resourceName) {
        try {
            InputStream translationsStream = getClass().getClassLoader().getResourceAsStream(resourceName);
            if (translationsStream == null) {
                System.err.println("Failed to load " + resourceName + " - file not found");
                return;
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Map<String, String>> loadedTranslations = mapper.readValue(translationsStream, Map.class);
            translations.putAll(loadedTranslations);
            System.out.println("Loaded translations for languages: " + translations.keySet());
        } catch (Exception e) {
            System.err.println("Failed to load translations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<String, String> getTranslations(String lang) {
        return translations.getOrDefault(lang, translations.get("nl"));
    }
}
