package com.suirfiruvet.service;

import com.suirfiruvet.dto.CitaDTO;
import com.suirfiruvet.dto.CitaRequest;
import com.suirfiruvet.entity.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CitaService {

    @Inject
    EntityManager em;

    public List<CitaDTO> getByUid(String uid) {
        List<Cita> citas = uid != null
            ? em.createQuery("FROM Cita c WHERE c.cliente.uid = :uid", Cita.class)
                .setParameter("uid", uid).getResultList()
            : em.createQuery("FROM Cita", Cita.class).getResultList();
        return citas.stream().map(this::toDTO).toList();
    }

    public Optional<CitaDTO> getById(Long id) {
        return Optional.ofNullable(em.find(Cita.class, id)).map(this::toDTO);
    }

    @Transactional
    public Optional<CitaDTO> crear(CitaRequest req) {
        List<Cliente> clientes = em.createQuery("FROM Cliente c WHERE c.uid = :uid", Cliente.class)
            .setParameter("uid", req.getUid()).getResultList();
        if (clientes.isEmpty()) return Optional.empty();

        Cita cita = new Cita();
        cita.setTipoCita(em.find(TipoCita.class, req.getIdTipoCita()));
        cita.setFecha(req.getFecha());
        cita.setComentario(req.getComentario());
        cita.setMascota(em.find(Mascota.class, req.getIdMascota()));
        cita.setCliente(clientes.get(0));
        cita.setClinica(em.find(Clinica.class, req.getIdClinica()));
        em.persist(cita);
        return Optional.of(toDTO(cita));
    }

    @Transactional
    public Optional<CitaDTO> modificar(Long id, CitaRequest req) {
        Cita cita = em.find(Cita.class, id);
        if (cita == null) return Optional.empty();
        if (!cita.getCliente().getUid().equals(req.getUid())) return Optional.of(new CitaDTO()); // marcador de forbidden

        cita.setTipoCita(em.find(TipoCita.class, req.getIdTipoCita()));
        cita.setFecha(req.getFecha());
        cita.setComentario(req.getComentario());
        cita.setMascota(em.find(Mascota.class, req.getIdMascota()));
        cita.setClinica(em.find(Clinica.class, req.getIdClinica()));
        return Optional.of(toDTO(cita));
    }

    @Transactional
    public int eliminar(Long id, String uid) {
        Cita cita = em.find(Cita.class, id);
        if (cita == null) return 404;
        if (!cita.getCliente().getUid().equals(uid)) return 403;
        em.remove(cita);
        return 204;
    }

    private CitaDTO toDTO(Cita c) {
        CitaDTO dto = new CitaDTO();
        dto.setIdCita(c.getIdCita());
        dto.setNombreTipoCita(c.getTipoCita().getNombre());
        dto.setFecha(c.getFecha());
        dto.setComentario(c.getComentario());
        dto.setIdMascota(c.getMascota().getId());
        dto.setNombreMascota(c.getMascota().getNombMas());
        dto.setIdCliente(c.getCliente().getId());
        dto.setNombreCliente(c.getCliente().getNombCli() + " " + c.getCliente().getApeCli());
        dto.setIdClinica(c.getClinica().getId());
        dto.setNombreClinica(c.getClinica().getNombre());
        return dto;
    }
}
