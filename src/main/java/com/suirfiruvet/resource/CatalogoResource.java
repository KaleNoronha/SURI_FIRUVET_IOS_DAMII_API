package com.suirfiruvet.resource;

import com.suirfiruvet.entity.TipoCita;
import com.suirfiruvet.entity.TipoMascota;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CatalogoResource {

    @Inject
    EntityManager em;

    @GET
    @Path("/tipos-cita")
    public Response getTiposCita() {
        return Response.ok(em.createQuery("FROM TipoCita", TipoCita.class).getResultList()).build();
    }

    @GET
    @Path("/tipos-mascota")
    public Response getTiposMascota() {
        return Response.ok(em.createQuery("FROM TipoMascota", TipoMascota.class).getResultList()).build();
    }
}
