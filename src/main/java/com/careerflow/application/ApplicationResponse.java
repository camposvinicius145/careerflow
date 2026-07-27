package com.careerflow.application;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ApplicationResponse(
        UUID id,
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
        String notes,
        Instant createdAt,
        Instant updatedAt,
        long version) {

    static ApplicationResponse from(JobApplication application) {
        return new ApplicationResponse(
                application.getId(),
                application.getCompany(),
                application.getRoleTitle(),
                application.getLocation(),
                application.getJobUrl(),
                application.getSource(),
                application.getStatus(),
                application.getWorkModel(),
                application.getPriority(),
                application.getSalaryMin(),
                application.getSalaryMax(),
                application.getSalaryCurrency(),
                application.getAppliedAt(),
                application.getNextActionAt(),
                application.getNotes(),
                application.getCreatedAt(),
                application.getUpdatedAt(),
                application.getVersion());
    }
}
