package com.surifiruvet.service;

import com.surifiruvet.entity.EventoLog;
import com.surifiruvet.messaging.AuditoriaEvent;
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
        log.setEstado("REGISTRADO");
        em.persist(log);
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
