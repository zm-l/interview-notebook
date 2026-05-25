package com.notebook.model;

import java.util.List;
import java.util.UUID;

public class QuestionEntry {
    private String id;
    private String company;
    private String question;
    private String answer;
    private List<String> topics;

    public QuestionEntry(){}

    public QuestionEntry(String company, String question) {
        this.id = UUID.randomUUID().toString();
        this.company = company;
        this.question = question;
    }

    public String getId() {
        return id;
    }
    public String getCompany() {
        return company;
    }
    public String getQuestion() {
        return question;
    }
    public String getAnswer() {
        return answer;
    }
    public List<String> getTopics() {
        return topics;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

}
