package com.careerflow.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.careerflow.auth.UserAccount;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class JobApplicationTest {

    private final UserAccount owner =
            UserAccount.create("dev@example.com", "Dev Example", "encoded-password");

    @Test
    void movingToAppliedAutomaticallyRegistersApplicationDate() {
        JobApplication application = JobApplication.create(owner, data(ApplicationStatus.SAVED));

        ApplicationStatus previous = application.moveTo(ApplicationStatus.APPLIED);

        assertThat(previous).isEqualTo(ApplicationStatus.SAVED);
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.APPLIED);
        assertThat(application.getAppliedAt()).isEqualTo(LocalDate.now());
    }

    @Test
    void rejectsAnInvertedSalaryRange() {
        ApplicationData invalid =
                new ApplicationData(
                        "Acme",
                        "Java Engineer",
                        "Remote",
                        null,
                        null,
                        ApplicationStatus.SAVED,
                        WorkModel.REMOTE,
                        Priority.MEDIUM,
                        20_000,
                        10_000,
                        "BRL",
                        null,
                        null,
                        null);

        assertThatThrownBy(() -> JobApplication.create(owner, invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mínimo");
    }

    @Test
    void normalizesRequiredTextAndCurrency() {
        ApplicationData input =
                new ApplicationData(
                        "  Acme  ",
                        "  Java Engineer ",
                        null,
                        null,
                        null,
                        ApplicationStatus.SAVED,
                        WorkModel.REMOTE,
                        Priority.LOW,
                        null,
                        null,
                        "usd",
                        null,
                        null,
                        null);

        JobApplication application = JobApplication.create(owner, input);

        assertThat(application.getCompany()).isEqualTo("Acme");
        assertThat(application.getRoleTitle()).isEqualTo("Java Engineer");
        assertThat(application.getSalaryCurrency()).isEqualTo("USD");
    }

    private static ApplicationData data(ApplicationStatus status) {
        return new ApplicationData(
                "Acme",
                "Java Engineer",
                "Remote",
                null,
                "LinkedIn",
                status,
                WorkModel.REMOTE,
                Priority.HIGH,
                12_000,
                18_000,
                "BRL",
                null,
                null,
                "Great opportunity");
    }
}
