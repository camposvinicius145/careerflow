package com.careerflow.common.config;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component("dates")
public class DateDisplay {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("dd MMM yyyy", PT_BR);
    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("dd MMM, HH:mm", PT_BR);

    public String date(LocalDate date) {
        return date == null ? "—" : DATE.format(date);
    }

    public String dateTime(Instant instant) {
        return instant == null
                ? "—"
                : DATE_TIME.format(instant.atZone(ZoneId.systemDefault()));
    }

    public String relative(Instant instant) {
        if (instant == null) {
            return "Sem data";
        }
        LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        if (date.equals(today)) {
            return "Hoje";
        }
        if (date.equals(today.plusDays(1))) {
            return "Amanhã";
        }
        if (date.equals(today.minusDays(1))) {
            return "Ontem";
        }
        return DATE.format(date);
    }
}
