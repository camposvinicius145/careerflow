package com.careerflow.auth;

import java.util.UUID;

public record CurrentUser(UUID id, String email, String displayName) {}
