package com.careerflow.application;

import com.careerflow.auth.CurrentUser;
import com.careerflow.auth.CurrentUserService;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final CurrentUserService currentUserService;

    public ApplicationController(
            ApplicationService applicationService, CurrentUserService currentUserService) {
        this.applicationService = applicationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    String board(Authentication authentication, Model model) {
        CurrentUser user = currentUserService.require(authentication);
        List<JobApplication> applications = applicationService.list(user.id());
        Map<ApplicationStatus, List<JobApplication>> columns =
                new EnumMap<>(ApplicationStatus.class);
        Arrays.stream(ApplicationStatus.values())
                .forEach(
                        status ->
                                columns.put(
                                        status,
                                        applications.stream()
                                                .filter(item -> item.getStatus() == status)
                                                .toList()));
        model.addAttribute("columns", columns);
        model.addAttribute("statuses", ApplicationStatus.values());
        model.addAttribute("currentUser", user);
        return "applications/board";
    }

    @GetMapping("/new")
    String createPage(Authentication authentication, Model model) {
        model.addAttribute("applicationForm", new ApplicationForm());
        model.addAttribute("currentUser", currentUserService.require(authentication));
        addOptions(model);
        return "applications/form";
    }

    @PostMapping
    String create(
            @Valid @ModelAttribute ApplicationForm applicationForm,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        CurrentUser user = currentUserService.require(authentication);
        if (salaryRangeIsInvalid(applicationForm)) {
            bindingResult.rejectValue(
                    "salaryMax",
                    "salary.range",
                    "O valor máximo deve ser maior ou igual ao mínimo.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", user);
            addOptions(model);
            return "applications/form";
        }
        applicationService.create(user.id(), applicationForm.toData());
        redirectAttributes.addFlashAttribute("success", "Candidatura adicionada ao seu fluxo.");
        return "redirect:/applications";
    }

    @GetMapping("/{id}/edit")
    String editPage(
            @PathVariable UUID id, Authentication authentication, Model model) {
        CurrentUser user = currentUserService.require(authentication);
        JobApplication application = applicationService.get(id, user.id());
        model.addAttribute("jobApplication", application);
        model.addAttribute("applicationForm", ApplicationForm.from(application));
        model.addAttribute("events", applicationService.history(id, user.id()));
        model.addAttribute("currentUser", user);
        addOptions(model);
        return "applications/form";
    }

    @PostMapping("/{id}")
    String update(
            @PathVariable UUID id,
            @Valid @ModelAttribute ApplicationForm applicationForm,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        CurrentUser user = currentUserService.require(authentication);
        if (salaryRangeIsInvalid(applicationForm)) {
            bindingResult.rejectValue(
                    "salaryMax",
                    "salary.range",
                    "O valor máximo deve ser maior ou igual ao mínimo.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("jobApplication", applicationService.get(id, user.id()));
            model.addAttribute("events", applicationService.history(id, user.id()));
            model.addAttribute("currentUser", user);
            addOptions(model);
            return "applications/form";
        }
        applicationService.update(id, user.id(), applicationForm.toData());
        redirectAttributes.addFlashAttribute("success", "Alterações salvas.");
        return "redirect:/applications/" + id + "/edit";
    }

    @PostMapping("/{id}/delete")
    String delete(
            @PathVariable UUID id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        CurrentUser user = currentUserService.require(authentication);
        applicationService.delete(id, user.id());
        redirectAttributes.addFlashAttribute("success", "Candidatura removida.");
        return "redirect:/applications";
    }

    private static boolean salaryRangeIsInvalid(ApplicationForm form) {
        return form.getSalaryMin() != null
                && form.getSalaryMax() != null
                && form.getSalaryMin() > form.getSalaryMax();
    }

    private static void addOptions(Model model) {
        model.addAttribute("statuses", ApplicationStatus.values());
        model.addAttribute("workModels", WorkModel.values());
        model.addAttribute("priorities", Priority.values());
    }
}
