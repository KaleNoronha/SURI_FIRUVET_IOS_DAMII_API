package com.surifiruvet.resource;

import java.util.List;
import com.surifiruvet.dto.TipoCitaDTO;
import com.surifiruvet.service.CatalogoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CatalogoResource {

    @Inject
    CatalogoService catalogoService;

    @GET
    @Path("/tipos-cita")
    public Response getTiposCita() {
        return Response.ok(catalogoService.getTiposCita()).build();
    }

    @GET
    @Path("/tipos-mascota")
    public Response getTiposMascota() {
        return Response.ok(catalogoService.getTiposMascota()).build();
    }
    
    //-----------NUEVO------------
    @GET
    @Path("/tipos-cita/arbol")
    public Response getTiposCitaArbol() {
        List<TipoCitaDTO> arbol = catalogoService.getTiposCitaArbol();
        return Response.ok(arbol).build();
    }
}
