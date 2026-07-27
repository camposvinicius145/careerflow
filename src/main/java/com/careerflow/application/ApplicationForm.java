package com.careerflow.application;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

public class ApplicationForm {

    @NotBlank(message = "Informe a empresa.")
    @Size(max = 140)
    private String company;

    @NotBlank(message = "Informe o cargo.")
    @Size(max = 160)
    private String roleTitle;

    @Size(max = 160)
    private String location;

    @URL(message = "Informe uma URL válida.")
    @Size(max = 500)
    private String jobUrl;

    @Size(max = 80)
    private String source;

    @NotNull private ApplicationStatus status = ApplicationStatus.SAVED;

    @NotNull private WorkModel workModel = WorkModel.REMOTE;

    @NotNull private Priority priority = Priority.MEDIUM;

    @Min(value = 0, message = "O valor não pode ser negativo.")
    @Max(value = 10_000_000, message = "O valor informado é muito alto.")
    private Integer salaryMin;

    @Min(value = 0, message = "O valor não pode ser negativo.")
    @Max(value = 10_000_000, message = "O valor informado é muito alto.")
    private Integer salaryMax;

    @Size(min = 3, max = 3, message = "Use o código de 3 letras, como BRL.")
    private String salaryCurrency = "BRL";

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate appliedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime nextActionAt;

    @Size(max = 3000, message = "Use até 3.000 caracteres.")
    private String notes;

    public static ApplicationForm from(JobApplication application) {
        var form = new ApplicationForm();
        form.company = application.getCompany();
        form.roleTitle = application.getRoleTitle();
        form.location = application.getLocation();
        form.jobUrl = application.getJobUrl();
        form.source = application.getSource();
        form.status = application.getStatus();
        form.workModel = application.getWorkModel();
        form.priority = application.getPriority();
        form.salaryMin = application.getSalaryMin();
        form.salaryMax = application.getSalaryMax();
        form.salaryCurrency = application.getSalaryCurrency();
        form.appliedAt = application.getAppliedAt();
        form.nextActionAt =
                application.getNextActionAt() == null
                        ? null
                        : LocalDateTime.ofInstant(
                                application.getNextActionAt(), ZoneId.systemDefault());
        form.notes = application.getNotes();
        return form;
    }

    ApplicationData toData() {
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
                nextActionAt == null
                        ? null
                        : nextActionAt.atZone(ZoneId.systemDefault()).toInstant(),
                notes);
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public WorkModel getWorkModel() {
        return workModel;
    }

    public void setWorkModel(WorkModel workModel) {
        this.workModel = workModel;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Integer getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(Integer salaryMin) {
        this.salaryMin = salaryMin;
    }

    public Integer getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(Integer salaryMax) {
        this.salaryMax = salaryMax;
    }

    public String getSalaryCurrency() {
        return salaryCurrency;
    }

    public void setSalaryCurrency(String salaryCurrency) {
        this.salaryCurrency = salaryCurrency;
    }

    public LocalDate getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDate appliedAt) {
        this.appliedAt = appliedAt;
    }

    public LocalDateTime getNextActionAt() {
        return nextActionAt;
    }

    public void setNextActionAt(LocalDateTime nextActionAt) {
        this.nextActionAt = nextActionAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
