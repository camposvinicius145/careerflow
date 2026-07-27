package com.careerflow.application;

import com.careerflow.auth.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "job_application")
public class JobApplication {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserAccount owner;

    @Column(nullable = false, length = 140)
    private String company;

    @Column(name = "role_title", nullable = false, length = 160)
    private String roleTitle;

    @Column(length = 160)
    private String location;

    @Column(name = "job_url", length = 500)
    private String jobUrl;

    @Column(length = 80)
    private String source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApplicationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_model", nullable = false, length = 20)
    private WorkModel workModel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority;

    @Column(name = "salary_min")
    private Integer salaryMin;

    @Column(name = "salary_max")
    private Integer salaryMax;

    @Column(name = "salary_currency", nullable = false, length = 3)
    private String salaryCurrency;

    @Column(name = "applied_at")
    private LocalDate appliedAt;

    @Column(name = "next_action_at")
    private Instant nextActionAt;

    @Column(length = 3000)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    protected JobApplication() {}

    public static JobApplication create(UserAccount owner, ApplicationData data) {
        var application = new JobApplication();
        application.id = UUID.randomUUID();
        application.owner = Objects.requireNonNull(owner);
        application.apply(data);
        Instant now = Instant.now();
        application.createdAt = now;
        application.updatedAt = now;
        return application;
    }

    public void update(ApplicationData data) {
        apply(data);
        updatedAt = Instant.now();
    }

    public ApplicationStatus moveTo(ApplicationStatus newStatus) {
        ApplicationStatus previous = status;
        status = Objects.requireNonNull(newStatus);
        if (newStatus == ApplicationStatus.APPLIED && appliedAt == null) {
            appliedAt = LocalDate.now();
        }
        updatedAt = Instant.now();
        return previous;
    }

    private void apply(ApplicationData data) {
        Objects.requireNonNull(data);
        company = required(data.company(), "company");
        roleTitle = required(data.roleTitle(), "roleTitle");
        location = clean(data.location());
        jobUrl = clean(data.jobUrl());
        source = clean(data.source());
        status = Objects.requireNonNull(data.status());
        workModel = Objects.requireNonNull(data.workModel());
        priority = Objects.requireNonNull(data.priority());
        validateSalary(data.salaryMin(), data.salaryMax());
        salaryMin = data.salaryMin();
        salaryMax = data.salaryMax();
        salaryCurrency =
                data.salaryCurrency() == null || data.salaryCurrency().isBlank()
                        ? "BRL"
                        : data.salaryCurrency().trim().toUpperCase();
        appliedAt = data.appliedAt();
        nextActionAt = data.nextActionAt();
        notes = clean(data.notes());
    }

    private static void validateSalary(Integer minimum, Integer maximum) {
        if (minimum != null && minimum < 0 || maximum != null && maximum < 0) {
            throw new IllegalArgumentException("A faixa salarial não pode ser negativa.");
        }
        if (minimum != null && maximum != null && minimum > maximum) {
            throw new IllegalArgumentException("O salário mínimo deve ser menor que o máximo.");
        }
    }

    private static String required(String value, String field) {
        String cleaned = clean(value);
        if (cleaned == null) {
            throw new IllegalArgumentException(field + " é obrigatório.");
        }
        return cleaned;
    }

    private static String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public UUID getId() {
        return id;
    }

    public UserAccount getOwner() {
        return owner;
    }

    public String getCompany() {
        return company;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public String getLocation() {
        return location;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public String getSource() {
        return source;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public WorkModel getWorkModel() {
        return workModel;
    }

    public Priority getPriority() {
        return priority;
    }

    public Integer getSalaryMin() {
        return salaryMin;
    }

    public Integer getSalaryMax() {
        return salaryMax;
    }

    public String getSalaryCurrency() {
        return salaryCurrency;
    }

    public LocalDate getAppliedAt() {
        return appliedAt;
    }

    public Instant getNextActionAt() {
        return nextActionAt;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public long getVersion() {
        return version;
    }
}
