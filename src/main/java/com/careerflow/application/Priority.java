package com.careerflow.application;

public enum Priority {
    LOW("Baixa"),
    MEDIUM("Média"),
    HIGH("Alta");

    private final String label;

    Priority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
