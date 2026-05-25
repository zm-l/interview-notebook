package com.notebook.generator;

import com.notebook.model.QuestionEntry;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MarkdownGenerator {

    public void generate(List<QuestionEntry> entries, String fileName, boolean groupByTopic) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# Interview Notebook\n\n");

        if (groupByTopic) {
            Map<String, List<QuestionEntry>> topicMap = entries.stream()
                    .filter(e -> e.getTopics() != null)
                    .flatMap(e -> e.getTopics().stream().map(topic -> Map.entry(topic, e)))
                    .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

            topicMap.forEach((topic, questions) -> {
                markdown.append("## ").append(topic).append("\n\n");
                appendQuestions(markdown, questions);
            });
        } else {
            Map<String, List<QuestionEntry>> companyMap = entries.stream()
                    .collect(Collectors.groupingBy(QuestionEntry::getCompany));
            companyMap.forEach((company, questions) -> {
                markdown.append("## ").append(company).append("\n\n");
                appendQuestions(markdown, questions);
            });
        }

        try {
            Files.writeString(Path.of(fileName), markdown.toString());
            System.out.println("Generated " + fileName);
        } catch (Exception e) {
            System.err.println("Failed to write markdown file: " + e.getMessage());
        }
    }

    private void appendQuestions(StringBuilder markdown, List<QuestionEntry> questions) {
        for (QuestionEntry question : questions) {
            markdown.append("### Q: ").append(question.getQuestion()).append("\n\n");
            markdown.append("**Company:** ").append(question.getCompany()).append("\n\n");
            if (question.getTopics() != null) {
                markdown.append("**Topics:** ").append(question.getTopics().toString()).append("\n\n");
            }

            if (question.getAnswer() != null) {
                String shiftedAnswer = question.getAnswer().replaceAll("(?m)^(#+)", "###$1");
                markdown.append(shiftedAnswer).append("\n\n");
            }
        }
    }
}
