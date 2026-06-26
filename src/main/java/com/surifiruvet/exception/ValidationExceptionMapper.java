package com.surifiruvet.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Convierte errores de @Valid a respuestas 400 con mensajes legibles.
 * Patrón tomado del proyecto de referencia con DTO validation.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        String errores = e.getConstraintViolations().stream()
                .map(cv -> cv.getMessage())
                .collect(Collectors.joining(", "));

        return Response.status(400)
                .entity(Map.of("error", errores))
                .build();
    }
}
