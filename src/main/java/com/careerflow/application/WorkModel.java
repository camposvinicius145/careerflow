package com.careerflow.application;

public enum WorkModel {
    REMOTE("Remoto"),
    HYBRID("Híbrido"),
    ONSITE("Presencial");

    private final String label;

    WorkModel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
