package com.suirfiruvet.resource;

import com.suirfiruvet.dto.MascotaDTO;
import com.suirfiruvet.entity.Mascota;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

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
}
