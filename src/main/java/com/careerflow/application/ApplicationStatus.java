package com.careerflow.application;

public enum ApplicationStatus {
    SAVED("Em vista", "bookmark"),
    APPLIED("Candidatado", "send"),
    INTERVIEW("Entrevista", "calendar"),
    OFFER("Oferta", "sparkles"),
    CLOSED("Encerrada", "archive");

    private final String label;
    private final String icon;

    ApplicationStatus(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isActive() {
        return this != CLOSED;
    }
}
