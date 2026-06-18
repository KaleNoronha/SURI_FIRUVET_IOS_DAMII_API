package com.suirfiruvet.resource;

import com.suirfiruvet.service.MascotaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/mascotas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class MascotaResource {

    @Inject
    MascotaService mascotaService;

    @GET
    public Response getByUid(@QueryParam("uid") String uid) {
        return Response.ok(mascotaService.getByUid(uid)).build();
    }
}
