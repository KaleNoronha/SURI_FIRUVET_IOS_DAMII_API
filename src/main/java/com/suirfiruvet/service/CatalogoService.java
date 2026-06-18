package com.suirfiruvet.service;

import com.suirfiruvet.entity.TipoCita;
import com.suirfiruvet.entity.TipoMascota;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;

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
}
