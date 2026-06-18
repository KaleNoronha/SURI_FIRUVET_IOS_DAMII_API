package com.suirfiruvet.service;

import com.suirfiruvet.dto.ClienteRequest;
import com.suirfiruvet.entity.Cliente;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class ClienteService {

    @Inject
    EntityManager em;

    public Optional<Cliente> getById(Long id) {
        return Optional.ofNullable(em.find(Cliente.class, id));
    }

    public Optional<Cliente> getByUid(String uid) {
        return em.createQuery("FROM Cliente c WHERE c.uid = :uid", Cliente.class)
            .setParameter("uid", uid)
            .getResultStream()
            .findFirst();
    }

    public boolean existeByUid(String uid) {
        return !em.createQuery("FROM Cliente c WHERE c.uid = :uid", Cliente.class)
            .setParameter("uid", uid)
            .getResultList().isEmpty();
    }

    @Transactional
    public Cliente crear(ClienteRequest req) {
        Cliente cliente = new Cliente();
        cliente.setNombCli(req.getNombCli());
        cliente.setApeCli(req.getApeCli());
        cliente.setFecNac(req.getFecNac());
        cliente.setUid(req.getUid());
        em.persist(cliente);
        return cliente;
    }
}
