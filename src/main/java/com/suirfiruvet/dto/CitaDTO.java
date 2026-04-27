package com.suirfiruvet.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CitaDTO {
    private Long idCita;
    private String nombreTipoCita;
    private LocalDateTime fecha;
    private String comentario;
    private Long idMascota;
    private String nombreMascota;
    private Long idCliente;
    private String nombreCliente;
    private Long idClinica;
    private String nombreClinica;
}
