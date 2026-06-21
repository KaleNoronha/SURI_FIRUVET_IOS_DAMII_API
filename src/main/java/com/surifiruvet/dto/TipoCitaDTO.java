package com.surifiruvet.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class TipoCitaDTO {
    private Long id;
    private String nombre;
    private Long padreId;
    
    // recursiva
    private List<TipoCitaDTO> hijos = new ArrayList<>();
}