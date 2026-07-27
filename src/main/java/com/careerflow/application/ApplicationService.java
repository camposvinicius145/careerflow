package com.careerflow.application;

import com.careerflow.auth.UserAccount;
import com.careerflow.auth.UserAccountRepository;
import com.careerflow.common.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationService {

    private final JobApplicationRepository applications;
    private final ApplicationEventRepository events;
    private final UserAccountRepository users;

    public ApplicationService(
            JobApplicationRepository applications,
            ApplicationEventRepository events,
            UserAccountRepository users) {
        this.applications = applications;
        this.events = events;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<JobApplication> list(UUID ownerId) {
        return applications.findAllByOwnerIdOrderByUpdatedAtDesc(ownerId);
    }

    @Transactional(readOnly = true)
    public JobApplication get(UUID applicationId, UUID ownerId) {
        return findOwned(applicationId, ownerId);
    }

    @Transactional(readOnly = true)
    public List<ApplicationEvent> history(UUID applicationId, UUID ownerId) {
        findOwned(applicationId, ownerId);
        return events.findAllByApplicationIdOrderByOccurredAtDesc(applicationId);
    }

    @Transactional
    public JobApplication create(UUID ownerId, ApplicationData data) {
        UserAccount owner =
                users.findById(ownerId)
                        .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        JobApplication application = applications.save(JobApplication.create(owner, data));
        events.save(
                ApplicationEvent.of(
                        application,
                        EventType.CREATED,
                        "Candidatura criada em " + application.getStatus().getLabel() + "."));
        return application;
    }

    @Transactional
    public JobApplication update(UUID applicationId, UUID ownerId, ApplicationData data) {
        JobApplication application = findOwned(applicationId, ownerId);
        application.update(data);
        events.save(
                ApplicationEvent.of(
                        application, EventType.UPDATED, "Informações da candidatura atualizadas."));
        return application;
    }

    @Transactional
    public JobApplication move(
            UUID applicationId, UUID ownerId, ApplicationStatus destinationStatus) {
        JobApplication application = findOwned(applicationId, ownerId);
        ApplicationStatus previous = application.moveTo(destinationStatus);
        if (previous != destinationStatus) {
            events.save(
                    ApplicationEvent.of(
                            application,
                            EventType.STATUS_CHANGED,
                            "Movida de "
                                    + previous.getLabel()
                                    + " para "
                                    + destinationStatus.getLabel()
                                    + "."));
        }
        return application;
    }

    @Transactional
    public void delete(UUID applicationId, UUID ownerId) {
        JobApplication application = findOwned(applicationId, ownerId);
        applications.delete(application);
    }

    private JobApplication findOwned(UUID applicationId, UUID ownerId) {
        return applications
                .findByIdAndOwnerId(applicationId, ownerId)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Candidatura não encontrada ou sem acesso."));
    }
}
