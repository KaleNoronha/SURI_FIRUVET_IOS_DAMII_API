package com.surifiruvet.service;

import com.surifiruvet.entity.Clinica;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import com.surifiruvet.dto.ClinicaDTO;

@ApplicationScoped
public class ClinicaService {

    @Inject
    EntityManager em;

    public List<Clinica> getAll() {
        return em.createQuery("FROM Clinica", Clinica.class).getResultList();
    }
    
    //NUEVO
    public List<ClinicaDTO> getClinicasArbol() {
        List<Clinica> raices = em.createQuery(
            "FROM Clinica c WHERE c.sedePadre IS NULL", 
            Clinica.class).getResultList();
        
        return raices.stream()
            .map(this::buildClinicaTree)
            .toList();
    }

  //NUEVO
    private ClinicaDTO buildClinicaTree(Clinica clinica) {
        ClinicaDTO dto = new ClinicaDTO();
        dto.setId(clinica.getId());
        dto.setNombre(clinica.getNombre());
        dto.setDireccion(clinica.getDireccion());
        dto.setSedePadreId(clinica.getSedePadre() != null ? clinica.getSedePadre().getId() : null);
        
        if (clinica.getSedesHijas() != null && !clinica.getSedesHijas().isEmpty()) {
            List<ClinicaDTO> hijosDTO = clinica.getSedesHijas().stream()
                .map(this::buildClinicaTree)  //Recursividad aquí
                .toList();
            dto.setSedesHijas(hijosDTO);
        }
        return dto;
    }
}
