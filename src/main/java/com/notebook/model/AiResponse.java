package com.notebook.model;

import java.util.List;

public record AiResponse(
        String question,
        String answer,
        List<String> topics
) {
}