package com.surifiruvet.messaging;

import com.surifiruvet.service.EventoLogService;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AuditoriaEventConsumer {

    private static final Logger LOG = Logger.getLogger(AuditoriaEventConsumer.class);

    @Inject
    EventoLogService eventoLogService;

    @Incoming("auditoria-in")
    public void recibir(Buffer buffer) {
        String mensaje = buffer.toString();
        JsonObject json = new JsonObject(mensaje);

        AuditoriaEvent evento = new AuditoriaEvent(
                json.getString("tipoEvento"),
                json.getString("modulo"),
                json.getString("accion"),
                json.getString("entidad"),
                json.getLong("idRegistro"),
                json.getString("uid"),
                json.getString("descripcion"),
                json.getString("datosJson"),
                json.getString("generadoEn")
        );

        LOG.infof("Evento auditoría recibido: entidad=%s accion=%s id=%s",
                evento.entidad(), evento.accion(), evento.idRegistro());

        eventoLogService.registrarAuditoria(evento);
    }
}