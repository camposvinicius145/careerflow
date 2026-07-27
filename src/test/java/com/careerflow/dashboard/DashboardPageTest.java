package com.careerflow.dashboard;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.careerflow.application.ApplicationData;
import com.careerflow.application.ApplicationService;
import com.careerflow.application.ApplicationStatus;
import com.careerflow.application.JobApplication;
import com.careerflow.application.Priority;
import com.careerflow.application.WorkModel;
import com.careerflow.auth.UserAccount;
import com.careerflow.auth.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DashboardPageTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserAccountRepository users;
    @Autowired private ApplicationService applicationService;
    @Autowired private PasswordEncoder passwordEncoder;

    private UserAccount owner;

    @BeforeEach
    void setUp() {
        owner =
                users.save(
                        UserAccount.create(
                                "test@careerflow.dev",
                                "Test Developer",
                                passwordEncoder.encode("test-password")));
        applicationService.create(owner.getId(), data(ApplicationStatus.APPLIED));
    }

    @Test
    void rendersDashboardWithRealDatabaseData() throws Exception {
        mockMvc.perform(get("/dashboard").with(user("test@careerflow.dev")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Test Developer")))
                .andExpect(
                        content()
                                .string(
                                        org.hamcrest.Matchers.containsString(
                                                "Candidaturas ativas")));
    }

    @Test
    void rendersKanbanBoard() throws Exception {
        mockMvc.perform(get("/applications").with(user("test@careerflow.dev")))
                .andExpect(status().isOk())
                .andExpect(view().name("applications/board"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Meu pipeline")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Acme")));
    }

    @Test
    void rendersNewApplicationForm() throws Exception {
        mockMvc.perform(get("/applications/new").with(user("test@careerflow.dev")))
                .andExpect(status().isOk())
                .andExpect(view().name("applications/form"))
                .andExpect(
                        content()
                                .string(
                                        org.hamcrest.Matchers.containsString(
                                                "Adicionar candidatura")));
    }

    @Test
    void rendersApplicationDetailsAndHistory() throws Exception {
        JobApplication application = applicationService.list(owner.getId()).getFirst();

        mockMvc.perform(
                        get("/applications/{id}/edit", application.getId())
                                .with(user("test@careerflow.dev")))
                .andExpect(status().isOk())
                .andExpect(view().name("applications/form"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Acme")))
                .andExpect(
                        content()
                                .string(
                                        org.hamcrest.Matchers.containsString(
                                                "Linha do tempo")));
    }

    @Test
    void movesCardThroughRestApi() throws Exception {
        JobApplication application = applicationService.list(owner.getId()).getFirst();

        mockMvc.perform(
                        patch("/api/v1/applications/{id}/status", application.getId())
                                .with(user("test@careerflow.dev"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"status\":\"INTERVIEW\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INTERVIEW"));
    }

    @Test
    void redirectsAnonymousUsersToLogin() throws Exception {
        mockMvc.perform(get("/dashboard")).andExpect(status().is3xxRedirection());
    }

    private static ApplicationData data(ApplicationStatus status) {
        return new ApplicationData(
                "Acme",
                "Java Engineer",
                "Brasil",
                null,
                "LinkedIn",
                status,
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
