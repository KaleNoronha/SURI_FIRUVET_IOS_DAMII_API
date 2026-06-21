package com.surifiruvet.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class MascotaDTO {
    private Long id;
    private String nombMas;
    private Long idTipoMascota;
    private String nombreTipo;
    private Long idCliente;
    

    // NUEVO
    private Long padreId;
    private List<MascotaDTO> hijos = new ArrayList<>();
}
