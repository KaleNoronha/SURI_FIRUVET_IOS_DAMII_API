package com.suirfiruvet.resource;

import com.suirfiruvet.dto.ClienteRequest;
import com.suirfiruvet.entity.Cliente;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/api/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ClienteResource {

    @Inject
    EntityManager em;

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Cliente cliente = em.find(Cliente.class, id);
        if (cliente == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(cliente).build();
    }

    @GET
    @Path("/uid/{uid}")
    public Response getByUid(@PathParam("uid") String uid) {
        return em.createQuery("FROM Cliente c WHERE c.uid = :uid", Cliente.class)
            .setParameter("uid", uid)
            .getResultStream()
            .findFirst()
            .map(c -> Response.ok(c).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    public Response crear(ClienteRequest req) {
        if (req == null || req.getNombCli() == null || req.getNombCli().isBlank())
            return Response.status(400).entity(Map.of("error", "El campo nombCli es requerido.")).build();
        if (req.getApeCli() == null || req.getApeCli().isBlank())
            return Response.status(400).entity(Map.of("error", "El campo apeCli es requerido.")).build();
        if (req.getUid() == null || req.getUid().isBlank())
            return Response.status(400).entity(Map.of("error", "El campo uid es requerido.")).build();

        boolean existe = !em.createQuery("FROM Cliente c WHERE c.uid = :uid", Cliente.class)
            .setParameter("uid", req.getUid())
            .getResultList().isEmpty();
        if (existe)
            return Response.status(409).entity(Map.of("error", "Ya existe un cliente con ese uid.")).build();

        Cliente cliente = new Cliente();
        cliente.setNombCli(req.getNombCli());
        cliente.setApeCli(req.getApeCli());
        cliente.setFecNac(req.getFecNac());
        cliente.setUid(req.getUid());
        em.persist(cliente);
        return Response.status(Response.Status.CREATED).entity(cliente).build();
    }
}
