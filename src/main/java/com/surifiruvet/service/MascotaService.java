package com.surifiruvet.service;

import com.surifiruvet.dto.MascotaDTO;
import com.surifiruvet.entity.Mascota;
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
    
    
    //NUEVO
    public List<MascotaDTO> getMascotasArbol() {
        List<Mascota> raices = em.createQuery(
            "FROM Mascota m WHERE m.padre IS NULL", 
            Mascota.class).getResultList();
        
        return raices.stream()
            .map(this::buildPedigreeTree)
            .toList();
    }

    //NUEVO
    private MascotaDTO buildPedigreeTree(Mascota mascota) {
        MascotaDTO dto = new MascotaDTO();
        dto.setId(mascota.getId());
        dto.setNombMas(mascota.getNombMas());
        dto.setPadreId(mascota.getPadre() != null ? mascota.getPadre().getId() : null);
        
        if (mascota.getHijos() != null && !mascota.getHijos().isEmpty()) {
            List<MascotaDTO> hijosDTO = mascota.getHijos().stream()
                .map(this::buildPedigreeTree) //Aquí se repite
                .toList();
            dto.setHijos(hijosDTO);
        }
        return dto;
    }
    
}
