package com.careerflow.dashboard;

import com.careerflow.auth.CurrentUser;
import com.careerflow.auth.CurrentUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final CurrentUserService currentUserService;

    public DashboardController(
            DashboardService dashboardService, CurrentUserService currentUserService) {
        this.dashboardService = dashboardService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/")
    String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    String dashboard(Authentication authentication, Model model) {
        CurrentUser user = currentUserService.require(authentication);
        model.addAttribute("currentUser", user);
        model.addAttribute("dashboard", dashboardService.get(user.id()));
        return "dashboard";
    }
}
