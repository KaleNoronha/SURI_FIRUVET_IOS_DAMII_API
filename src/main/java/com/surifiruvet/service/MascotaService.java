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

    public List<MascotaDTO> getByUid(String uid) {
        List<Mascota> mascotas = uid != null
            ? em.createQuery("FROM Mascota m WHERE m.cliente.uid = :uid", Mascota.class)
                .setParameter("uid", uid).getResultList()
            : em.createQuery("FROM Mascota", Mascota.class).getResultList();

        return mascotas.stream().map(this::toDTO).toList();
    }
    
    public Optional<MascotaDTO> getById(Long id) {
        return Optional.ofNullable(em.find(Mascota.class, id)).map(this::toDTO);
    }
    
    @Transactional
    public Optional<MascotaDTO> crear(MascotaRequest req) {
        // Buscar cliente por ID
        Cliente cliente = em.find(Cliente.class, req.getIdCliente());
        if (cliente == null) {
            return Optional.empty(); // Cliente no existe
        }
        
        // Verificar que el TipoMascota exista
        TipoMascota tipoMascota = em.find(TipoMascota.class, req.getIdTipoMascota());
        if (tipoMascota == null) {
            return Optional.empty(); // Tipo de mascota no existe
        }
        
        Mascota mascota = new Mascota();
        mascota.setNombMas(req.getNombMas());
        mascota.setTipoMascota(tipoMascota);
        mascota.setCliente(cliente);
        em.persist(mascota);
        
        MascotaDTO dto = toDTO(mascota);
        publicarAuditoria(
                "MASCOTA_CREADA",
                "CREAR",
                "Se registró una nueva mascota",
                dto,
                req.getUid() != null ? req.getUid() : cliente.getUid()
        );
        
        return Optional.of(dto);
    }
    
    @Transactional
    public Optional<MascotaDTO> modificar(Long id, MascotaRequest req) {
        Mascota mascota = em.find(Mascota.class, id);
        if (mascota == null) return Optional.empty();
        if (!mascota.getCliente().getUid().equals(req.getUid())) return Optional.empty();
        
        mascota.setNombMas(req.getNombMas());
        mascota.setTipoMascota(em.find(TipoMascota.class, req.getIdTipoMascota()));
        
        MascotaDTO dto = toDTO(mascota);
        publicarAuditoria(
                "MASCOTA_MODIFICADA",
                "MODIFICAR",
                "Se modificó una mascota",
                dto,
                req.getUid()
        );
        
        return Optional.of(dto);
    }
    
    @Transactional
    public int eliminar(Long id, String uid) {
        Mascota mascota = em.find(Mascota.class, id);
        if (mascota == null) return 404;
        if (!mascota.getCliente().getUid().equals(uid)) return 403;
        
        MascotaDTO dto = toDTO(mascota);
        em.remove(mascota);
        
        publicarAuditoria(
                "MASCOTA_ELIMINADA",
                "ELIMINAR",
                "Se eliminó una mascota",
                dto,
                uid
        );
        
        return 204;
    }
    
    private void publicarAuditoria(
            String tipoEvento,
            String accion,
            String descripcion,
            MascotaDTO dto,
            String uid
    ) {
        auditoriaEventPublisher.publicar(
                AuditoriaEvent.crear(
                        tipoEvento,
                        "MASCOTAS",
                        accion,
                        "mascota",
                        dto.getId(),
                        uid,
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