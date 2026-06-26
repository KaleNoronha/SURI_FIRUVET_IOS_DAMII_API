package com.surifiruvet.service;

import com.surifiruvet.dto.ClienteRequest;
import com.surifiruvet.dto.UpdateClienteRequest;
import com.surifiruvet.dto.ClienteDTO;
import com.surifiruvet.entity.Cliente;
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
public class ClienteService {

    @Inject
    EntityManager em;

    @Inject
    AuditoriaEventPublisher auditoriaEventPublisher;

    public List<ClienteDTO> listar() {
        return em.createQuery("FROM Cliente", Cliente.class).getResultList()
                .stream().map(this::toDTO).toList();
    }

    public Optional<ClienteDTO> getById(Long id) {
        return Optional.ofNullable(em.find(Cliente.class, id)).map(this::toDTO);
    }

    public boolean existeByUid(String uid) {
        return !em.createQuery("FROM Cliente c WHERE c.uid = :uid", Cliente.class)
            .setParameter("uid", uid)
            .getResultList().isEmpty();
    }

    @Transactional
    public ClienteDTO crear(ClienteRequest req) {
        Cliente cliente = new Cliente();
        cliente.setNombCli(req.getNombCli());
        cliente.setApeCli(req.getApeCli());
        cliente.setFecNac(req.getFecNac());
        cliente.setUid(req.getUid());
        // Default rol = 1 (usuario)
        Long idRol = req.getIdRol() != null ? req.getIdRol() : 1L;
        cliente.setRol(em.find(com.surifiruvet.entity.Rol.class, idRol));
        em.persist(cliente);

        ClienteDTO dto = toDTO(cliente);
        publicarAuditoria("CLIENTE_CREADO", "CREAR", "Se registró un nuevo cliente", dto);
        return dto;
    }

    @Transactional
    public Optional<ClienteDTO> cambiarRol(Long id, Long idRol) {
        Cliente cliente = em.find(Cliente.class, id);
        if (cliente == null) return Optional.empty();
        com.surifiruvet.entity.Rol rol = em.find(com.surifiruvet.entity.Rol.class, idRol);
        if (rol == null) return Optional.empty();
        cliente.setRol(rol);
        return Optional.of(toDTO(cliente));
    }

    @Transactional
    public Optional<ClienteDTO> modificar(Long id, UpdateClienteRequest req) {
        Cliente cliente = em.find(Cliente.class, id);
        if (cliente == null) return Optional.empty();

        cliente.setNombCli(req.getNombCli());
        cliente.setApeCli(req.getApeCli());
        cliente.setFecNac(req.getFecNac());

        ClienteDTO dto = toDTO(cliente);
        publicarAuditoria("CLIENTE_MODIFICADO", "MODIFICAR", "Se modificó un cliente", dto);
        return Optional.of(dto);
    }

    @Transactional
    public int eliminar(Long id) {
        Cliente cliente = em.find(Cliente.class, id);
        if (cliente == null) return 404;

        long mascotas = em.createQuery("SELECT COUNT(m) FROM Mascota m WHERE m.cliente.id = :id", Long.class)
            .setParameter("id", id).getSingleResult();
        if (mascotas > 0) return 409;

        ClienteDTO dto = toDTO(cliente);
        em.remove(cliente);
        publicarAuditoria("CLIENTE_ELIMINADO", "ELIMINAR", "Se eliminó un cliente", dto);
        return 204;
    }

    private void publicarAuditoria(String tipoEvento, String accion, String descripcion, ClienteDTO dto) {
        auditoriaEventPublisher.publicar(
                AuditoriaEvent.crear(
                        tipoEvento,
                        "CLIENTES",
                        accion,
                        "cliente",
                        dto.getId(),
                        dto.getUid(),
                        descripcion,
                        JsonObject.mapFrom(dto).encode()
                )
        );
    }

    private ClienteDTO toDTO(Cliente c) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(c.getId());
        dto.setNombCli(c.getNombCli());
        dto.setApeCli(c.getApeCli());
        dto.setFecNac(c.getFecNac());
        dto.setUid(c.getUid());
        if (c.getRol() != null) {
            dto.setIdRol(c.getRol().getId());
            dto.setRolNombre(c.getRol().getNombre());
        } else {
            dto.setIdRol(1L);
            dto.setRolNombre("usuario");
        }
        return dto;
    }
}
