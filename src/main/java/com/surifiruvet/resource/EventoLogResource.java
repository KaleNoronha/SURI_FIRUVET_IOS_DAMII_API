package com.surifiruvet.resource;

import com.surifiruvet.entity.EventoLog;
import com.surifiruvet.service.EventoLogService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/logs")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class EventoLogResource {

    @Inject
    EventoLogService eventoLogService;

    @GET
    public List<EventoLog> listar() {
        return eventoLogService.listar();
    }

    @GET
    @Path("/usuario/{uid}")
    public List<EventoLog> porUsuario(@PathParam("uid") String uid) {
        return eventoLogService.listarPorUid(uid);
    }

    @GET
    @Path("/modulo/{modulo}")
    public List<EventoLog> porModulo(@PathParam("modulo") String modulo) {
        return eventoLogService.listarPorModulo(modulo);
    }

    @GET
    @Path("/entidad/{entidad}/registro/{id}")
    public List<EventoLog> porEntidad(
            @PathParam("entidad") String entidad,
            @PathParam("id") Long idRegistro) {
        return eventoLogService.listarPorEntidad(entidad, idRegistro);
    }
}