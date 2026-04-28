package com.suirfiruvet.resource;

import com.suirfiruvet.dto.MascotaDTO;
import com.suirfiruvet.dto.MascotaRequest;
import com.suirfiruvet.entity.Cliente;
import com.suirfiruvet.entity.Mascota;
import com.suirfiruvet.entity.TipoMascota;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/api/mascotas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class MascotaResource {

    @Inject
    EntityManager em;

    @GET
    public Response getByUid(@QueryParam("uid") String uid) {
        List<Mascota> mascotas;
        if (uid != null) {
            mascotas = em.createQuery(
                "FROM Mascota m WHERE m.cliente.uid = :uid", Mascota.class)
                .setParameter("uid", uid).getResultList();
        } else {
            mascotas = em.createQuery("FROM Mascota", Mascota.class).getResultList();
        }

        List<MascotaDTO> result = mascotas.stream().map(m -> {
            MascotaDTO dto = new MascotaDTO();
            dto.setId(m.getId());
            dto.setNombMas(m.getNombMas());
            dto.setIdTipoMascota(m.getTipoMascota().getId());
            dto.setNombreTipo(m.getTipoMascota().getNombre());
            dto.setIdCliente(m.getCliente().getId());
            return dto;
        }).toList();

        return Response.ok(result).build();
    }

    @POST
    @Transactional
    public Response crear(MascotaRequest req) {
        if (req.getUid() == null || req.getUid().isBlank())
            return Response.status(400).entity(Map.of("error", "El campo uid es requerido.")).build();
        if (req.getNombMas() == null || req.getNombMas().isBlank())
            return Response.status(400).entity(Map.of("error", "El campo nombMas es requerido.")).build();

        List<Cliente> clientes = em.createQuery("FROM Cliente c WHERE c.uid = :uid", Cliente.class)
            .setParameter("uid", req.getUid()).getResultList();
        if (clientes.isEmpty())
            return Response.status(404).entity(Map.of("error", "No se encontró cliente con ese uid.")).build();

        Mascota mascota = new Mascota();
        mascota.setNombMas(req.getNombMas());
        mascota.setTipoMascota(em.find(TipoMascota.class, req.getIdTipoMascota()));
        mascota.setCliente(clientes.get(0));
        mascota.setApodos(req.getApodos());
        mascota.setAlergias(req.getAlergias());
        em.persist(mascota);

        MascotaDTO dto = new MascotaDTO();
        dto.setId(mascota.getId());
        dto.setNombMas(mascota.getNombMas());
        dto.setIdTipoMascota(mascota.getTipoMascota().getId());
        dto.setNombreTipo(mascota.getTipoMascota().getNombre());
        dto.setIdCliente(mascota.getCliente().getId());
        dto.setApodos(mascota.getApodos());
        dto.setAlergias(mascota.getAlergias());
        return Response.status(Response.Status.CREATED).entity(dto).build();
    }
}
