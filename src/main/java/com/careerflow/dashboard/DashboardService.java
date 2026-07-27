package com.careerflow.dashboard;

import com.careerflow.application.ApplicationStatus;
import com.careerflow.application.JobApplication;
import com.careerflow.application.JobApplicationRepository;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private static final DateTimeFormatter WEEK_LABEL =
            DateTimeFormatter.ofPattern("dd MMM", Locale.forLanguageTag("pt-BR"));

    private final JobApplicationRepository applications;

    public DashboardService(JobApplicationRepository applications) {
        this.applications = applications;
    }

    @Transactional(readOnly = true)
    public DashboardView get(UUID ownerId) {
        List<JobApplication> all = applications.findAllByOwnerIdOrderByUpdatedAtDesc(ownerId);

        Map<ApplicationStatus, Long> pipeline = new EnumMap<>(ApplicationStatus.class);
        Arrays.stream(ApplicationStatus.values())
                .forEach(
                        status ->
                                pipeline.put(
                                        status,
                                        all.stream()
                                                .filter(application -> application.getStatus() == status)
                                                .count()));

        long active =
                all.stream().filter(application -> application.getStatus().isActive()).count();
        long interviews = pipeline.get(ApplicationStatus.INTERVIEW);
        long offers = pipeline.get(ApplicationStatus.OFFER);
        long submitted =
                all.stream()
                        .filter(application -> application.getStatus() != ApplicationStatus.SAVED)
                        .count();
        long responses = interviews + offers + pipeline.get(ApplicationStatus.CLOSED);
        int responseRate =
                submitted == 0 ? 0 : (int) Math.round((responses * 100.0) / submitted);

        List<JobApplication> recent = all.stream().limit(5).toList();
        List<JobApplication> upcoming =
                applications
                        .findTop5ByOwnerIdAndNextActionAtGreaterThanEqualOrderByNextActionAtAsc(
                                ownerId, Instant.now());

        return new DashboardView(
                active,
                interviews,
                offers,
                responseRate,
                pipeline,
                recent,
                upcoming,
                weeklySeries(all));
    }

    private static List<DashboardView.WeeklyPoint> weeklySeries(List<JobApplication> all) {
        LocalDate currentWeek =
                LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return java.util.stream.IntStream.rangeClosed(0, 5)
                .mapToObj(index -> currentWeek.minusWeeks(5L - index))
                .map(
                        weekStart -> {
                            LocalDate weekEnd = weekStart.plusDays(7);
                            long count =
                                    all.stream()
                                            .map(JobApplication::getAppliedAt)
                                            .filter(java.util.Objects::nonNull)
                                            .filter(
                                                    date ->
                                                            !date.isBefore(weekStart)
                                                                    && date.isBefore(weekEnd))
                                            .count();
                            return new DashboardView.WeeklyPoint(
                                    WEEK_LABEL.format(weekStart), count);
                        })
                .toList();
    }
}
