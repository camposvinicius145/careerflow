package com.careerflow.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.careerflow.auth.UserAccount;
import com.careerflow.auth.UserAccountRepository;
import com.careerflow.common.exception.ResourceNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock private JobApplicationRepository applications;
    @Mock private ApplicationEventRepository events;
    @Mock private UserAccountRepository users;

    private ApplicationService service;

    @BeforeEach
    void setUp() {
        service = new ApplicationService(applications, events, users);
    }

    @Test
    void createsApplicationAndAuditEventInOneUseCase() {
        UserAccount owner = UserAccount.create("owner@example.com", "Owner", "hash");
        when(users.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(applications.save(any(JobApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        JobApplication created = service.create(owner.getId(), data());

        assertThat(created.getOwner()).isEqualTo(owner);
        assertThat(created.getCompany()).isEqualTo("Acme");
        verify(events).save(any(ApplicationEvent.class));
    }

    @Test
    void preventsReadingAnotherUsersApplication() {
        UUID applicationId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        when(applications.findByIdAndOwnerId(applicationId, ownerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(applicationId, ownerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("sem acesso");
    }

    private static ApplicationData data() {
        return new ApplicationData(
                "Acme",
                "Java Engineer",
                "Brasil",
                null,
                "LinkedIn",
                ApplicationStatus.APPLIED,
                WorkModel.REMOTE,
                Priority.HIGH,
                null,
                null,
                "BRL",
                null,
                null,
                null);
    }
}
