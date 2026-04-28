package com.suirfiruvet.dto;

import lombok.Data;

@Data
public class MascotaDTO {
    private Long id;
    private String nombMas;
    private Long idTipoMascota;
    private String nombreTipo;
    private Long idCliente;
    private String apodos;
    private String alergias;
}
