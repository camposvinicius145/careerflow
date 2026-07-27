package com.careerflow.application;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationEventRepository extends JpaRepository<ApplicationEvent, UUID> {

    List<ApplicationEvent> findAllByApplicationIdOrderByOccurredAtDesc(UUID applicationId);
}
