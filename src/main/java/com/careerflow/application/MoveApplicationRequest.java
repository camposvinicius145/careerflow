package com.careerflow.application;

import jakarta.validation.constraints.NotNull;

public record MoveApplicationRequest(@NotNull ApplicationStatus status) {}
