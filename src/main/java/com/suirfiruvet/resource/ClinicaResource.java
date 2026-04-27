package com.suirfiruvet.resource;

import com.suirfiruvet.entity.Clinica;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/clinicas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ClinicaResource {

    @Inject
    EntityManager em;

    @GET
    public Response getAll() {
        return Response.ok(em.createQuery("FROM Clinica", Clinica.class).getResultList()).build();
    }
}
