package com.notebook.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notebook.model.QuestionEntry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Database {
    private static final String FILE_PATH = "database.json";
    private final ObjectMapper mapper = new ObjectMapper();

    public List<QuestionEntry> load() {
        if (!new File(FILE_PATH).exists()) {
            return new ArrayList<>();
        }

        try {
            QuestionEntry[] array = mapper.readValue(new File(FILE_PATH), QuestionEntry[].class);
            return new ArrayList<>(Arrays.asList(array));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void save(List<QuestionEntry> questionEntries) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), questionEntries);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
