package com.surifiruvet.dto;

import lombok.Data;

@Data
public class MascotaRequest {
    private String nombMas;
    private Long idTipoMascota;
    private Long idCliente;
    private String uid;
}