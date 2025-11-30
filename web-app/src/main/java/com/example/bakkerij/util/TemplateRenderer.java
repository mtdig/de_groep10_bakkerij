package com.example.bakkerij.util;

import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.loader.ResourceLocator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

public class TemplateRenderer {
    private final Jinjava jinjava;
    private final TranslationService translationService;
    private final String appVersion;

    public TemplateRenderer(TranslationService translationService, String appVersion) {
        this.translationService = translationService;
        this.appVersion = appVersion;
        
        JinjavaConfig config = JinjavaConfig.newBuilder().build();
        this.jinjava = new Jinjava(config);
        this.jinjava.setResourceLocator(new ResourceLocator() {
            @Override
            public String getString(String fullName, Charset encoding, JinjavaInterpreter interpreter) throws IOException {
                String path = fullName;
                if (!path.startsWith("templates/")) {
                    path = "templates/" + path;
                }
                InputStream is = getClass().getClassLoader().getResourceAsStream(path);
                if (is == null) {
                    throw new IOException("Template not found: " + path);
                }
                return new String(is.readAllBytes(), encoding);
            }
        });
    }

    public String render(String templatePath, Map<String, Object> context, String lang) {
        context.put("lang", lang);
        context.put("t", translationService.getTranslations(lang));
        context.put("version", appVersion);
        
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(templatePath);
            if (is == null) {
                throw new RuntimeException("Template not found: " + templatePath);
            }
            String template = new String(is.readAllBytes());
            return jinjava.render(template, context);
        } catch (Exception e) {
            throw new RuntimeException("Failed to render template: " + templatePath, e);
        }
    }
}
