package com.surifiruvet.resource;

import java.util.List;
import com.surifiruvet.dto.MascotaDTO;
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
    
    // NUEVO
    @GET
    @Path("/pedigree")
    public Response getPedigree() {
        List<MascotaDTO> arbol = mascotaService.getMascotasArbol();
        return Response.ok(arbol).build();
    }
}
