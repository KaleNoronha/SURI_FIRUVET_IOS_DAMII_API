package com.surifiruvet.service;

import com.surifiruvet.entity.TipoCita;
import com.surifiruvet.entity.TipoMascota;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import com.surifiruvet.dto.TipoCitaDTO;


@ApplicationScoped
public class CatalogoService {

    @Inject
    EntityManager em;

    public List<TipoCita> getTiposCita() {
        return em.createQuery("FROM TipoCita", TipoCita.class).getResultList();
    }

    public List<TipoMascota> getTiposMascota() {
        return em.createQuery("FROM TipoMascota", TipoMascota.class).getResultList();
    }
    
    
    
    //---------------------------NUEVO--------------------------
    
    public List<TipoCitaDTO> getTiposCitaArbol() {
        List<TipoCita> raices = em.createQuery(
            "FROM TipoCita t WHERE t.padre IS NULL", 
            TipoCita.class).getResultList();
        
        return raices.stream()
            .map(this::buildTipoCitaTree)
            .toList();
    }

    private TipoCitaDTO buildTipoCitaTree(TipoCita tipo) {
        TipoCitaDTO dto = new TipoCitaDTO();
        dto.setId(tipo.getId());
        dto.setNombre(tipo.getNombre());
        dto.setPadreId(tipo.getPadre() != null ? tipo.getPadre().getId() : null);
        
        if (tipo.getHijos() != null && !tipo.getHijos().isEmpty()) {
            List<TipoCitaDTO> hijosDTO = tipo.getHijos().stream()
                .map(this::buildTipoCitaTree)  //Recursividad
                .toList();
            dto.setHijos(hijosDTO);
        }
        return dto;
    }
}
