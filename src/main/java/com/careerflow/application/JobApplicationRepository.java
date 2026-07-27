package com.careerflow.application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

    List<JobApplication> findAllByOwnerIdOrderByUpdatedAtDesc(UUID ownerId);

    Optional<JobApplication> findByIdAndOwnerId(UUID id, UUID ownerId);

    long countByOwnerIdAndStatus(UUID ownerId, ApplicationStatus status);

    @EntityGraph(attributePaths = "owner")
    List<JobApplication> findTop5ByOwnerIdAndNextActionAtGreaterThanEqualOrderByNextActionAtAsc(
            UUID ownerId, Instant from);

    void deleteByIdAndOwnerId(UUID id, UUID ownerId);
}
