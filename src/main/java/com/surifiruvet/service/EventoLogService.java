package com.surifiruvet.service;

import com.surifiruvet.entity.EventoLog;
import com.surifiruvet.messaging.AuditoriaEvent;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class EventoLogService {

    @Inject
    EntityManager em;

    @Transactional
    public void registrarAuditoria(AuditoriaEvent evento) {
        EventoLog log = new EventoLog();

        log.setTipoEvento(evento.tipoEvento());
        log.setModulo(evento.modulo());
        log.setAccion(evento.accion());
        log.setEntidad(evento.entidad());
        log.setIdRegistro(evento.idRegistro());
        log.setUid(evento.uid());
        log.setDescripcion(evento.descripcion());
        log.setDatosJson(evento.datosJson());

        String mensajeJson = JsonObject.mapFrom(evento).encode();
        log.setMensajeJson(mensajeJson);
        log.setEstado("REGISTRADO");

        completarDatosEspecificos(log, evento);

        em.persist(log);
    }

    private void completarDatosEspecificos(EventoLog log, AuditoriaEvent evento) {
        if (evento.datosJson() == null || evento.datosJson().isBlank()) {
            return;
        }

        try {
            JsonObject datos = new JsonObject(evento.datosJson());

            if ("cita".equalsIgnoreCase(evento.entidad())) {
                Long idCita = obtenerLong(datos, "idCita");
                Long idCliente = obtenerLong(datos, "idCliente");
                Long idMascota = obtenerLong(datos, "idMascota");
                Long idClinica = obtenerLong(datos, "idClinica");

                log.setIdCita(idCita != null ? idCita : evento.idRegistro());
                log.setIdCliente(idCliente);
                log.setIdMascota(idMascota);
                log.setIdClinica(idClinica);

                log.setNombreCliente(datos.getString("nombreCliente"));
                log.setNombreMascota(datos.getString("nombreMascota"));
                log.setNombreClinica(datos.getString("nombreClinica"));
                log.setNombreTipoCita(datos.getString("nombreTipoCita"));
            } else if ("cliente".equalsIgnoreCase(evento.entidad())) {
                Long idCliente = obtenerLong(datos, "id");
                log.setIdCliente(idCliente != null ? idCliente : evento.idRegistro());
                log.setNombreCliente(datos.getString("nombreCompleto"));
            } else if ("mascota".equalsIgnoreCase(evento.entidad())) {
                Long idMascota = obtenerLong(datos, "id");
                Long idCliente = obtenerLong(datos, "idCliente");
                log.setIdMascota(idMascota != null ? idMascota : evento.idRegistro());
                log.setIdCliente(idCliente);
                log.setNombreMascota(datos.getString("nombMas"));
            }

        } catch (Exception e) {
            // Si datosJson no es un JSON válido, igual se guarda el log general.
        }
    }

    private Long obtenerLong(JsonObject json, String campo) {
        Object valor = json.getValue(campo);

        if (valor == null) {
            return null;
        }

        if (valor instanceof Number numero) {
            return numero.longValue();
        }

        try {
            return Long.parseLong(valor.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<EventoLog> listar() {
        return em.createQuery("FROM EventoLog e ORDER BY e.id DESC", EventoLog.class)
                .getResultList();
    }

    public List<EventoLog> listarPorUid(String uid) {
        return em.createQuery("FROM EventoLog e WHERE e.uid = :uid ORDER BY e.id DESC", EventoLog.class)
                .setParameter("uid", uid)
                .getResultList();
    }

    public List<EventoLog> listarPorModulo(String modulo) {
        return em.createQuery("FROM EventoLog e WHERE e.modulo = :modulo ORDER BY e.id DESC", EventoLog.class)
                .setParameter("modulo", modulo)
                .getResultList();
    }

    public List<EventoLog> listarPorEntidad(String entidad, Long idRegistro) {
        return em.createQuery("FROM EventoLog e WHERE e.entidad = :entidad AND e.idRegistro = :idRegistro ORDER BY e.id DESC", EventoLog.class)
                .setParameter("entidad", entidad)
                .setParameter("idRegistro", idRegistro)
                .getResultList();
    }
}