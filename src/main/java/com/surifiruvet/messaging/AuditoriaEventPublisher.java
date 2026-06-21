package com.surifiruvet.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AuditoriaEventPublisher {

    private static final Logger LOG = Logger.getLogger(AuditoriaEventPublisher.class);

    @Inject
    @Channel("auditoria-out")
    Emitter<AuditoriaEvent> auditoriaEmitter;

    public void publicar(AuditoriaEvent evento) {
        auditoriaEmitter.send(evento)
                .whenComplete((ok, error) -> {
                    if (error != null) {
                        LOG.error("No se pudo enviar evento de auditoría a RabbitMQ", error);
                    } else {
                        LOG.infof(
                                "Evento auditoría enviado: %s %s id=%s",
                                evento.entidad(),
                                evento.accion(),
                                evento.idRegistro()
                        );
                    }
                });
    }
}