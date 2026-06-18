package com.suirfiruvet.service;

import com.suirfiruvet.dto.MascotaDTO;
import com.suirfiruvet.entity.Mascota;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class MascotaService {

    @Inject
    EntityManager em;

    public List<MascotaDTO> getByUid(String uid) {
        List<Mascota> mascotas = uid != null
            ? em.createQuery("FROM Mascota m WHERE m.cliente.uid = :uid", Mascota.class)
                .setParameter("uid", uid).getResultList()
            : em.createQuery("FROM Mascota", Mascota.class).getResultList();

        return mascotas.stream().map(m -> {
            MascotaDTO dto = new MascotaDTO();
            dto.setId(m.getId());
            dto.setNombMas(m.getNombMas());
            dto.setIdTipoMascota(m.getTipoMascota().getId());
            dto.setNombreTipo(m.getTipoMascota().getNombre());
            dto.setIdCliente(m.getCliente().getId());
            return dto;
        }).toList();
    }
}
