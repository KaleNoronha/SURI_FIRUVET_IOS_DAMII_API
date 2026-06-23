package com.surifiruvet.resource;

import com.surifiruvet.dto.CitaRequest;
import com.surifiruvet.service.CitaService;
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
    public Response getByIdCliente(@QueryParam("idCliente") Long idCliente) {
        return Response.ok(citaService.getByIdCliente(idCliente)).build();
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
        int resultado = citaService.modificar(id, req);
        return switch (resultado) {
            case 404 -> Response.status(Response.Status.NOT_FOUND).entity(Map.of("error", "Cita no encontrada.")).build();
            case 403 -> Response.status(Response.Status.FORBIDDEN).entity(Map.of("error", "No tienes permiso para modificar esta cita.")).build();
            default -> Response.ok().build();
        };
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Long id, @QueryParam("idCliente") Long idCliente) {
        int status = citaService.eliminar(id, idCliente);
        return switch (status) {
            case 404 -> Response.status(Response.Status.NOT_FOUND).entity(Map.of("error", "Cita no encontrada.")).build();
            case 403 -> Response.status(Response.Status.FORBIDDEN).entity(Map.of("error", "No tienes permiso para eliminar esta cita.")).build();
            default -> Response.noContent().build();
        };
    }
}
