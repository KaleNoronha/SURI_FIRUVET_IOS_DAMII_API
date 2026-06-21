package com.surifiruvet.resource;

import com.surifiruvet.dto.MascotaRequest;
import com.surifiruvet.service.MascotaService;
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
    
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        return mascotaService.getById(id)
            .map(dto -> Response.ok(dto).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
    
    @POST
    public Response crear(MascotaRequest req) {
        return mascotaService.crear(req)
            .map(dto -> Response.status(Response.Status.CREATED).entity(dto).build())
            .orElse(Response.status(Response.Status.BAD_REQUEST).build());
    }
    
    @PUT
    @Path("/{id}")
    public Response modificar(@PathParam("id") Long id, MascotaRequest req) {
        return mascotaService.modificar(id, req)
            .map(dto -> Response.ok(dto).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
    
    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Long id, @QueryParam("uid") String uid) {
        int resultado = mascotaService.eliminar(id, uid);
        return switch (resultado) {
            case 404 -> Response.status(Response.Status.NOT_FOUND).build();
            case 403 -> Response.status(Response.Status.FORBIDDEN).build();
            default -> Response.status(Response.Status.NO_CONTENT).build();
        };
    }
}
