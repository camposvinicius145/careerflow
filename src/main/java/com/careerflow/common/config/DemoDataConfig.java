package com.careerflow.common.config;

import com.careerflow.application.ApplicationData;
import com.careerflow.application.ApplicationService;
import com.careerflow.application.ApplicationStatus;
import com.careerflow.application.Priority;
import com.careerflow.application.WorkModel;
import com.careerflow.auth.UserAccount;
import com.careerflow.auth.UserAccountRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "careerflow.demo-data", havingValue = "true")
public class DemoDataConfig implements ApplicationRunner {

    private final UserAccountRepository users;
    private final ApplicationService applicationService;
    private final PasswordEncoder passwordEncoder;

    public DemoDataConfig(
            UserAccountRepository users,
            ApplicationService applicationService,
            PasswordEncoder passwordEncoder) {
        this.users = users;
        this.applicationService = applicationService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (users.existsByEmailIgnoreCase("demo@careerflow.dev")) {
            return;
        }

        UserAccount demo =
                users.save(
                        UserAccount.create(
                                "demo@careerflow.dev",
                                "Marina Costa",
                                passwordEncoder.encode("demo123")));

        add(
                demo,
                "Mercado Livre",
                "Senior Java Engineer",
                ApplicationStatus.INTERVIEW,
                WorkModel.REMOTE,
                Priority.HIGH,
                9,
                2,
                "Entrevista técnica com o time de plataforma. Revisar system design.",
                "LinkedIn");
        add(
                demo,
                "Nubank",
                "Backend Software Engineer",
                ApplicationStatus.APPLIED,
                WorkModel.HYBRID,
                Priority.HIGH,
                4,
                4,
                "Aguardar retorno do recrutador.",
                "Página de carreiras");
        add(
                demo,
                "iFood",
                "Software Engineer II",
                ApplicationStatus.OFFER,
                WorkModel.REMOTE,
                Priority.HIGH,
                20,
                3,
                "Revisar pacote de benefícios e preparar contraproposta.",
                "Indicação");
        add(
                demo,
                "Thoughtworks",
                "Desenvolvedor Java",
                ApplicationStatus.SAVED,
                WorkModel.REMOTE,
                Priority.MEDIUM,
                null,
                null,
                "Personalizar currículo antes de aplicar.",
                "LinkedIn");
        add(
                demo,
                "VTEX",
                "Backend Engineer",
                ApplicationStatus.APPLIED,
                WorkModel.REMOTE,
                Priority.MEDIUM,
                15,
                null,
                "Case enviado.",
                "Gupy");
        add(
                demo,
                "Stone",
                "Java Developer",
                ApplicationStatus.CLOSED,
                WorkModel.HYBRID,
                Priority.LOW,
                31,
                null,
                "Processo encerrado após conversa inicial.",
                "LinkedIn");
        add(
                demo,
                "CloudWalk",
                "Senior Backend Engineer",
                ApplicationStatus.INTERVIEW,
                WorkModel.REMOTE,
                Priority.HIGH,
                12,
                1,
                "Live coding amanhã às 14h.",
                "Indicação");
        add(
                demo,
                "PicPay",
                "Software Engineer Java",
                ApplicationStatus.APPLIED,
                WorkModel.REMOTE,
                Priority.MEDIUM,
                25,
                6,
                "Fazer follow-up caso não haja retorno.",
                "LinkedIn");
        add(
                demo,
                "Zup Innovation",
                "Backend Developer",
                ApplicationStatus.CLOSED,
                WorkModel.HYBRID,
                Priority.LOW,
                38,
                null,
                "Vaga pausada pela empresa.",
                "Gupy");
    }

    private void add(
            UserAccount owner,
            String company,
            String role,
            ApplicationStatus status,
            WorkModel workModel,
            Priority priority,
            Integer appliedDaysAgo,
            Integer nextActionDays,
            String notes,
            String source) {
        LocalDate appliedAt =
                appliedDaysAgo == null ? null : LocalDate.now().minusDays(appliedDaysAgo);
        applicationService.create(
                owner.getId(),
                new ApplicationData(
                        company,
                        role,
                        "Brasil",
                        "https://example.com/jobs/" + company.toLowerCase().replace(" ", "-"),
                        source,
                        status,
                        workModel,
                        priority,
                        12_000,
                        18_000,
                        "BRL",
                        appliedAt,
                        nextActionDays == null
                                ? null
                                : LocalDate.now()
                                        .plusDays(nextActionDays)
                                        .atTime(10, 0)
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant(),
                        notes));
    }
}
