package com.careerflow.application;

import com.careerflow.auth.CurrentUser;
import com.careerflow.auth.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/applications")
@Tag(name = "Applications", description = "Gerencie o pipeline de candidaturas do usuário.")
public class ApplicationRestController {

    private final ApplicationService applicationService;
    private final CurrentUserService currentUserService;

    public ApplicationRestController(
            ApplicationService applicationService, CurrentUserService currentUserService) {
        this.applicationService = applicationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    @Operation(summary = "Lista todas as candidaturas do usuário autenticado")
    List<ApplicationResponse> list(Authentication authentication) {
        CurrentUser user = currentUserService.require(authentication);
        return applicationService.list(user.id()).stream().map(ApplicationResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma candidatura")
    ApplicationResponse get(@PathVariable UUID id, Authentication authentication) {
        CurrentUser user = currentUserService.require(authentication);
        return ApplicationResponse.from(applicationService.get(id, user.id()));
    }

    @PostMapping
    @Operation(summary = "Cria uma candidatura")
    ResponseEntity<ApplicationResponse> create(
            @Valid @RequestBody ApplicationRequest request, Authentication authentication) {
        CurrentUser user = currentUserService.require(authentication);
        JobApplication created = applicationService.create(user.id(), request.toData());
        return ResponseEntity.created(URI.create("/api/v1/applications/" + created.getId()))
                .body(ApplicationResponse.from(created));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Move uma candidatura para uma etapa do pipeline")
    ApplicationResponse move(
            @PathVariable UUID id,
            @Valid @RequestBody MoveApplicationRequest request,
            Authentication authentication) {
        CurrentUser user = currentUserService.require(authentication);
        return ApplicationResponse.from(applicationService.move(id, user.id(), request.status()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma candidatura")
    ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        CurrentUser user = currentUserService.require(authentication);
        applicationService.delete(id, user.id());
        return ResponseEntity.noContent().build();
    }
}
