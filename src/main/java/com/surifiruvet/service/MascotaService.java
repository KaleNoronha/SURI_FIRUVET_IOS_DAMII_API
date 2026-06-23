package com.surifiruvet.service;

import com.surifiruvet.dto.MascotaDTO;
import com.surifiruvet.dto.MascotaRequest;
import com.surifiruvet.entity.Mascota;
import com.surifiruvet.entity.Cliente;
import com.surifiruvet.entity.TipoMascota;
import com.surifiruvet.messaging.AuditoriaEvent;
import com.surifiruvet.messaging.AuditoriaEventPublisher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MascotaService {

    @Inject
    EntityManager em;

    @Inject
    AuditoriaEventPublisher auditoriaEventPublisher;

    public List<MascotaDTO> getByIdCliente(Long idCliente) {
        List<Mascota> mascotas = idCliente != null
            ? em.createQuery("FROM Mascota m WHERE m.cliente.id = :idCliente", Mascota.class)
                .setParameter("idCliente", idCliente).getResultList()
            : em.createQuery("FROM Mascota", Mascota.class).getResultList();
        return mascotas.stream().map(this::toDTO).toList();
    }

    public Optional<MascotaDTO> getById(Long id) {
        return Optional.ofNullable(em.find(Mascota.class, id)).map(this::toDTO);
    }

    @Transactional
    public Optional<MascotaDTO> crear(MascotaRequest req) {
        Cliente cliente = em.find(Cliente.class, req.getIdCliente());
        if (cliente == null) return Optional.empty();

        TipoMascota tipoMascota = em.find(TipoMascota.class, req.getIdTipoMascota());
        if (tipoMascota == null) return Optional.empty();

        Mascota mascota = new Mascota();
        mascota.setNombMas(req.getNombMas());
        mascota.setTipoMascota(tipoMascota);
        mascota.setCliente(cliente);
        em.persist(mascota);

        MascotaDTO dto = toDTO(mascota);
        publicarAuditoria("MASCOTA_CREADA", "CREAR", "Se registró una nueva mascota", dto, req.getIdCliente());
        return Optional.of(dto);
    }

    @Transactional
    public Optional<MascotaDTO> modificar(Long id, MascotaRequest req) {
        Mascota mascota = em.find(Mascota.class, id);
        if (mascota == null) return Optional.empty();
        if (!mascota.getCliente().getId().equals(req.getIdCliente())) return Optional.empty();

        mascota.setNombMas(req.getNombMas());
        mascota.setTipoMascota(em.find(TipoMascota.class, req.getIdTipoMascota()));

        MascotaDTO dto = toDTO(mascota);
        publicarAuditoria("MASCOTA_MODIFICADA", "MODIFICAR", "Se modificó una mascota", dto, req.getIdCliente());
        return Optional.of(dto);
    }

    @Transactional
    public int eliminar(Long id, Long idCliente) {
        Mascota mascota = em.find(Mascota.class, id);
        if (mascota == null) return 404;
        if (!mascota.getCliente().getId().equals(idCliente)) return 403;

        MascotaDTO dto = toDTO(mascota);
        em.remove(mascota);
        publicarAuditoria("MASCOTA_ELIMINADA", "ELIMINAR", "Se eliminó una mascota", dto, idCliente);
        return 204;
    }

    private void publicarAuditoria(String tipoEvento, String accion, String descripcion, MascotaDTO dto, Long idCliente) {
        auditoriaEventPublisher.publicar(
                AuditoriaEvent.crear(
                        tipoEvento,
                        "MASCOTAS",
                        accion,
                        "mascota",
                        dto.getId(),
                        idCliente.toString(),
                        descripcion,
                        JsonObject.mapFrom(dto).encode()
                )
        );
    }

    private MascotaDTO toDTO(Mascota m) {
        MascotaDTO dto = new MascotaDTO();
        dto.setId(m.getId());
        dto.setNombMas(m.getNombMas());
        dto.setIdTipoMascota(m.getTipoMascota().getId());
        dto.setNombreTipo(m.getTipoMascota().getNombre());
        dto.setIdCliente(m.getCliente().getId());
        return dto;
    }
}
