package com.suirfiruvet.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CitaRequest {
    private Long idTipoCita;
    private LocalDateTime fecha;
    private String comentario;
    private Long idMascota;
    private Long idCliente;
    private String uid;
    private Long idClinica;
}
