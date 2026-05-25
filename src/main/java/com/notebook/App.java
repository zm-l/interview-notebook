package com.notebook;

import com.notebook.generator.MarkdownGenerator;
import com.notebook.model.AiResponse;
import com.notebook.model.QuestionEntry;
import com.notebook.repository.Database;
import com.notebook.service.AiService;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "interview-notebook", mixinStandardHelpOptions = true, version = "1.0", description = "Automated Java Interview Notebook")
public class App implements Callable<Integer> {
    @Option(names = {"-a", "--add"}, description = "Company name to add questions to")
    private String addCompany;

    @Option(names = {"-q", "--question"}, description = "The interview question")
    private String questionText;

    @Option(names = {"-g", "--generate"}, description = "Fetch missing answers from AI")
    private boolean generateAnswers;

    @Option(names = {"-m", "--markdown"}, description = "Generate markdown file")
    private boolean generateMarkdown;

    @Option(names = {"-t", "--topic"}, description = "Group questions by topic")
    private boolean sortByByTopic;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        Database db  = new Database();
        List<QuestionEntry> entries = db.load();

        if (addCompany != null && questionText != null) {
            entries.add(new QuestionEntry(addCompany, questionText));
            db.save(entries);
            System.out.println("Added question to " + addCompany + ". Status: PENDING");
        }

        if (generateAnswers) {
            AiService ai = new AiService();
            for (QuestionEntry entry : entries) {
                if (entry.getAnswer() == null) {
                    System.out.println("Fetching answer for: " + entry.getQuestion());
                    AiResponse response = ai.generateAnswer(entry.getQuestion());
                    entry.setAnswer(response.answer());
                    entry.setTopics(response.topics());
                    entry.setQuestion(response.question());
                    db.save(entries);
                }
            }
            System.out.println("Generating answers using AI...");
        }

        if (generateMarkdown) {
            String fileName = sortByByTopic ? "interview-notebook-by-topic.md" : "interview-notebook-by-company.md";
            new MarkdownGenerator().generate(entries, fileName, sortByByTopic);
        }

        return 0;
    }
}
