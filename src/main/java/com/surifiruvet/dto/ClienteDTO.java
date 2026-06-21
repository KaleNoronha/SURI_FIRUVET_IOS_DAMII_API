package com.surifiruvet.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ClienteDTO {
    private Long id;
    private String nombCli;
    private String apeCli;
    private LocalDate fecNac;
    private String uid;
    private String nombreCompleto;
    
    public void setNombreCompleto() {
        this.nombreCompleto = (nombCli != null ? nombCli : "") + " " + (apeCli != null ? apeCli : "");
    }
}