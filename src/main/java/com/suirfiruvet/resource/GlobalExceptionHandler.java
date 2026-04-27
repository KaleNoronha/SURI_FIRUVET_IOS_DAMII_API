package com.suirfiruvet.resource;

import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Map;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        Throwable cause = getRootCause(e);

        if (cause instanceof ConstraintViolationException) {
            return error(400, "Datos inválidos o incompletos. Verifica los campos enviados.");
        }
        if (e instanceof PersistenceException) {
            return error(400, "Error al procesar los datos. Verifica los campos enviados.");
        }
        return error(500, "Error interno del servidor.");
    }

    private Throwable getRootCause(Throwable t) {
        while (t.getCause() != null) t = t.getCause();
        return t;
    }

    private Response error(int status, String mensaje) {
        return Response.status(status)
            .entity(Map.of("error", mensaje))
            .build();
    }
}
