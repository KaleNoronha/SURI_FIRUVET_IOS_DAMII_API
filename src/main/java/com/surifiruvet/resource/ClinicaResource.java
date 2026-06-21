package com.surifiruvet.resource;

import java.util.List;

import com.surifiruvet.dto.ClinicaDTO;
import com.surifiruvet.service.ClinicaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/clinicas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ClinicaResource {

    @Inject
    ClinicaService clinicaService;

    @GET
    public Response getAll() {
        return Response.ok(clinicaService.getAll()).build();
    }
    
 // NUEVO
    @GET
    @Path("/arbol")
    public Response getClinicasArbol() {
        List<ClinicaDTO> arbol = clinicaService.getClinicasArbol();
        return Response.ok(arbol).build();
    }
}
