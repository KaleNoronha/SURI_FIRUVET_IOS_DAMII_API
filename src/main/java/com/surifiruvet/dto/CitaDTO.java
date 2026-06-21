package com.surifiruvet.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    
 // NUEVO
    private Long citaAnteriorId;
    private List<CitaDTO> citasSeguimiento = new ArrayList<>();
}
