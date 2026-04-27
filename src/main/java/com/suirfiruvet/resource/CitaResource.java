package com.suirfiruvet.resource;

import com.suirfiruvet.dto.CitaDTO;
import com.suirfiruvet.dto.CitaRequest;
import com.suirfiruvet.entity.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/api/citas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CitaResource {

    @Inject
    EntityManager em;

    @GET
    public Response getByUid(@QueryParam("uid") String uid) {
        List<Cita> citas = uid != null
            ? em.createQuery("FROM Cita c WHERE c.cliente.uid = :uid", Cita.class)
                .setParameter("uid", uid).getResultList()
            : em.createQuery("FROM Cita", Cita.class).getResultList();

        List<CitaDTO> result = citas.stream().map(this::toDTO).toList();
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    public Response getDetalle(@PathParam("id") Long id) {
        Cita cita = em.find(Cita.class, id);
        if (cita == null) return Response.status(404).entity(Map.of("error", "Cita no encontrada.")).build();
        return Response.ok(toDTO(cita)).build();
    }

    @POST
    @Transactional
    public Response crear(CitaRequest req) {
        List<Cliente> clientes = em.createQuery("FROM Cliente c WHERE c.uid = :uid", Cliente.class)
            .setParameter("uid", req.getUid()).getResultList();
        if (clientes.isEmpty())
            return Response.status(404).entity(Map.of("error", "No se encontró cliente con ese uid.")).build();

        Cita cita = new Cita();
        cita.setTipoCita(em.find(TipoCita.class, req.getIdTipoCita()));
        cita.setFecha(req.getFecha());
        cita.setComentario(req.getComentario());
        cita.setMascota(em.find(Mascota.class, req.getIdMascota()));
        cita.setCliente(clientes.get(0));
        cita.setClinica(em.find(Clinica.class, req.getIdClinica()));
        em.persist(cita);
        return Response.status(Response.Status.CREATED).entity(toDTO(cita)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response modificar(@PathParam("id") Long id, CitaRequest req) {
        Cita cita = em.find(Cita.class, id);
        if (cita == null) return Response.status(404).entity(Map.of("error", "Cita no encontrada.")).build();

        if (!cita.getCliente().getUid().equals(req.getUid()))
            return Response.status(403).entity(Map.of("error", "No tienes permiso para modificar esta cita.")).build();

        cita.setTipoCita(em.find(TipoCita.class, req.getIdTipoCita()));
        cita.setFecha(req.getFecha());
        cita.setComentario(req.getComentario());
        cita.setMascota(em.find(Mascota.class, req.getIdMascota()));
        cita.setClinica(em.find(Clinica.class, req.getIdClinica()));
        return Response.ok(toDTO(cita)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response eliminar(@PathParam("id") Long id, @QueryParam("uid") String uid) {
        Cita cita = em.find(Cita.class, id);
        if (cita == null) return Response.status(404).entity(Map.of("error", "Cita no encontrada.")).build();

        if (!cita.getCliente().getUid().equals(uid))
            return Response.status(403).entity(Map.of("error", "No tienes permiso para eliminar esta cita.")).build();

        em.remove(cita);
        return Response.noContent().build();
    }

    private CitaDTO toDTO(Cita c) {
        CitaDTO dto = new CitaDTO();
        dto.setIdCita(c.getIdCita());
        dto.setNombreTipoCita(c.getTipoCita().getNombre());
        dto.setFecha(c.getFecha());
        dto.setComentario(c.getComentario());
        dto.setIdMascota(c.getMascota().getId());
        dto.setNombreMascota(c.getMascota().getNombMas());
        dto.setIdCliente(c.getCliente().getId());
        dto.setNombreCliente(c.getCliente().getNombCli() + " " + c.getCliente().getApeCli());
        dto.setIdClinica(c.getClinica().getId());
        dto.setNombreClinica(c.getClinica().getNombre());
        return dto;
    }
}
