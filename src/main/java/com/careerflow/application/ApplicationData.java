package com.careerflow.application;

import java.time.Instant;
import java.time.LocalDate;

public record ApplicationData(
        String company,
        String roleTitle,
        String location,
        String jobUrl,
        String source,
        ApplicationStatus status,
        WorkModel workModel,
        Priority priority,
        Integer salaryMin,
        Integer salaryMax,
        String salaryCurrency,
        LocalDate appliedAt,
        Instant nextActionAt,
        String notes) {}
