package com.surifiruvet.messaging;

import java.time.LocalDateTime;

public record AuditoriaEvent(
        String tipoEvento,
        String modulo,
        String accion,
        String entidad,
        Long idRegistro,
        String uid,
        String descripcion,
        String datosJson,
        String generadoEn
) {
    public static AuditoriaEvent crear(
            String tipoEvento,
            String modulo,
            String accion,
            String entidad,
            Long idRegistro,
            String uid,
            String descripcion,
            String datosJson
    ) {
        return new AuditoriaEvent(
                tipoEvento,
                modulo,
                accion,
                entidad,
                idRegistro,
                uid,
                descripcion,
                datosJson,
                LocalDateTime.now().toString()
        );
    }
}