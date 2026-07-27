package com.careerflow.dashboard;

import com.careerflow.application.ApplicationStatus;
import com.careerflow.application.JobApplication;
import java.util.List;
import java.util.Map;

public record DashboardView(
        long activeApplications,
        long interviews,
        long offers,
        int responseRate,
        Map<ApplicationStatus, Long> pipeline,
        List<JobApplication> recentApplications,
        List<JobApplication> upcomingActions,
        List<WeeklyPoint> weeklySeries) {

    public record WeeklyPoint(String label, long value) {}
}
