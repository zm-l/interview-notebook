package com.notebook.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notebook.model.AiResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class AiService {
    private final HttpClient client;
    private final ObjectMapper mapper;
    private final String apiKey;

    public AiService() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        this.apiKey = System.getenv("ANTHROPIC_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("ANTHROPIC_API_KEY environment variable is not set");
        }
    }

    public AiResponse generateAnswer(String question) {
        try {
            String systemPrompt = "You are an elite Senior Backend Software Engineer and an expert technical mentor. Your goal is to provide exceptional, interview-ready answers to Software Engineering (SWE) questions. \n" +
                    "You possess deep expertise in high-performance computing, low-latency systems (e.g., trading engines), distributed architecture (e.g., Kafka, gRPC), and rigorous algorithmic optimization. " +
                    "Answer the interview question clearly, you can provide code examples if necessary. " +
                    "Determine 1 to 3 relevant technical topics for this question. " +
                    "Rephrase the question to make it more clear. " +
                    "You MUST respond ONLY with a raw, valid JSON object strictly matching this schema: " +
                    "{question\": \"" + question + "\", \"answer\": \"your markdown answer string\", \"topics\": [\"topic1\", \"topic2\"]}. " +
                    "Do not include any other text outside the JSON.";

            String payload = mapper.writeValueAsString(Map.of(
                    "model", "claude-haiku-4-5",
                    "max_tokens", 2048,
                    "system", systemPrompt,
                    "messages", new Object[]{
                            Map.of("role", "user", "content", question)
                    }
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.anthropic.com/v1/messages"))
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode jsonResponse = mapper.readTree(response.body());
            String content = jsonResponse.get("content").get(0).path("text").asText();

            int startIndex = content.indexOf('{');
            int endIndex = content.lastIndexOf('}');

            if (startIndex != -1 && endIndex != -1) {
                content = content.substring(startIndex, endIndex + 1);
            }

            return mapper.readValue(content, AiResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("API Call Failed: " + e.getMessage(), e);
        }
    }

}
