package com.careerflow.common.exception;

import com.careerflow.application.ApplicationRestController;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = ApplicationRestController.class)
public class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail notFound(ResourceNotFoundException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado",
                exception.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler({ConflictException.class, IllegalArgumentException.class})
    ProblemDetail conflict(RuntimeException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Regra de negócio inválida",
                exception.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        ProblemDetail detail =
                problem(
                        HttpStatus.BAD_REQUEST,
                        "Dados inválidos",
                        "Revise os campos informados.",
                        request.getRequestURI());
        Map<String, String> errors = new LinkedHashMap<>();
        exception
                .getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        detail.setProperty("errors", errors);
        return detail;
    }

    private static ProblemDetail problem(
            HttpStatus status, String title, String message, String path) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, message);
        detail.setTitle(title);
        detail.setInstance(URI.create(path));
        return detail;
    }
}
