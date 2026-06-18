package com.suirfiruvet.service;

import com.suirfiruvet.entity.Clinica;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class ClinicaService {

    @Inject
    EntityManager em;

    public List<Clinica> getAll() {
        return em.createQuery("FROM Clinica", Clinica.class).getResultList();
    }
}
