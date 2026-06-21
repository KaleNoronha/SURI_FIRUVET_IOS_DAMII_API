package com.surifiruvet.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ClinicaDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private Long sedePadreId;
    
    // 👇 Lista recursiva de sedes hijas
    private List<ClinicaDTO> sedesHijas = new ArrayList<>();
}