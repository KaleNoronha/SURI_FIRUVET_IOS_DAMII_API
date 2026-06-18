package com.suirfiruvet.resource;

import com.suirfiruvet.dto.CitaRequest;
import com.suirfiruvet.service.CitaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/api/citas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CitaResource {

    @Inject
    CitaService citaService;

    @GET
    public Response getByUid(@QueryParam("uid") String uid) {
        return Response.ok(citaService.getByUid(uid)).build();
    }

    @GET
    @Path("/{id}")
    public Response getDetalle(@PathParam("id") Long id) {
        return citaService.getById(id)
            .map(dto -> Response.ok(dto).build())
            .orElse(Response.status(404).entity(Map.of("error", "Cita no encontrada.")).build());
    }

    @POST
    public Response crear(CitaRequest req) {
        return citaService.crear(req)
            .map(dto -> Response.status(Response.Status.CREATED).entity(dto).build())
            .orElse(Response.status(404).entity(Map.of("error", "No se encontró cliente con ese uid.")).build());
    }

    @PUT
    @Path("/{id}")
    public Response modificar(@PathParam("id") Long id, CitaRequest req) {
        var result = citaService.modificar(id, req);
        if (result.isEmpty())
            return Response.status(404).entity(Map.of("error", "Cita no encontrada.")).build();
        var dto = result.get();
        if (dto.getIdCita() == null)
            return Response.status(403).entity(Map.of("error", "No tienes permiso para modificar esta cita.")).build();
        return Response.ok(dto).build();
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Long id, @QueryParam("uid") String uid) {
        int status = citaService.eliminar(id, uid);
        if (status == 404) return Response.status(404).entity(Map.of("error", "Cita no encontrada.")).build();
        if (status == 403) return Response.status(403).entity(Map.of("error", "No tienes permiso para eliminar esta cita.")).build();
        return Response.noContent().build();
    }
}
