package com.careerflow.application;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.validator.constraints.URL;

public record ApplicationRequest(
        @NotBlank @Size(max = 140) String company,
        @NotBlank @Size(max = 160) String roleTitle,
        @Size(max = 160) String location,
        @URL @Size(max = 500) String jobUrl,
        @Size(max = 80) String source,
        @NotNull ApplicationStatus status,
        @NotNull WorkModel workModel,
        @NotNull Priority priority,
        @Min(0) @Max(10_000_000) Integer salaryMin,
        @Min(0) @Max(10_000_000) Integer salaryMax,
        @Pattern(regexp = "[A-Za-z]{3}") String salaryCurrency,
        LocalDate appliedAt,
        Instant nextActionAt,
        @Size(max = 3000) String notes) {

    ApplicationData toData() {
        if (salaryMin != null && salaryMax != null && salaryMin > salaryMax) {
            throw new IllegalArgumentException(
                    "salaryMax deve ser maior ou igual a salaryMin.");
        }
        return new ApplicationData(
                company,
                roleTitle,
                location,
                jobUrl,
                source,
                status,
                workModel,
                priority,
                salaryMin,
                salaryMax,
                salaryCurrency,
                appliedAt,
                nextActionAt,
                notes);
    }
}
