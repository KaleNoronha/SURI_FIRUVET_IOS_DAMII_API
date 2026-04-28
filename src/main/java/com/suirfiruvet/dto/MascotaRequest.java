package com.suirfiruvet.dto;

import lombok.Data;

@Data
public class MascotaRequest {
    private String uid;
    private String nombMas;
    private Long idTipoMascota;
    private String apodos;
    private String alergias;
}
