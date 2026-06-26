package com.surifiruvet.resource;

import com.surifiruvet.dto.ClienteRequest;
import com.surifiruvet.service.ClienteService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
    ClienteService clienteService;

    @GET
    public Response listar() {
        return Response.ok(clienteService.listar()).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        return clienteService.getById(id)
            .map(c -> Response.ok(c).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response crear(ClienteRequest req) {
        if (req == null || req.getNombCli() == null || req.getNombCli().isBlank())
            return Response.status(400).entity(Map.of("error", "El campo nombCli es requerido.")).build();
        if (req.getApeCli() == null || req.getApeCli().isBlank())
            return Response.status(400).entity(Map.of("error", "El campo apeCli es requerido.")).build();
        if (req.getUid() == null || req.getUid().isBlank())
            return Response.status(400).entity(Map.of("error", "El campo uid es requerido.")).build();

        if (clienteService.existeByUid(req.getUid()))
            return Response.status(409).entity(Map.of("error", "Ya existe un cliente con ese uid.")).build();

        return Response.status(Response.Status.CREATED).entity(clienteService.crear(req)).build();
    }

    @PUT
    @Path("/{id}")
    public Response modificar(@PathParam("id") Long id, ClienteRequest req) {
        return clienteService.modificar(id, req)
            .map(dto -> Response.ok(dto).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PATCH
    @Path("/{id}/rol")
    public Response cambiarRol(@PathParam("id") Long id, Map<String, Long> body) {
        Long idRol = body.get("idRol");
        if (idRol == null || (idRol != 1L && idRol != 2L))
            return Response.status(400).entity(Map.of("error", "idRol inválido. Use 1 (usuario) o 2 (administrador).")).build();
        return clienteService.cambiarRol(id, idRol)
            .map(dto -> Response.ok(dto).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Long id) {
        int resultado = clienteService.eliminar(id);
        return switch (resultado) {
            case 404 -> Response.status(Response.Status.NOT_FOUND).build();
            default -> Response.status(Response.Status.NO_CONTENT).build();
        };
    }
}
