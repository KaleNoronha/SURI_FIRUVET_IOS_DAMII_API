package com.suirfiruvet.resource;

import com.suirfiruvet.dto.ClienteRequest;
import com.suirfiruvet.service.ClienteService;
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
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        return clienteService.getById(id)
            .map(c -> Response.ok(c).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/uid/{uid}")
    public Response getByUid(@PathParam("uid") String uid) {
        return clienteService.getByUid(uid)
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
}
