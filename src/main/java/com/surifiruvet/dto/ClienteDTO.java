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
    private Long idRol;       // 1=usuario, 2=administrador
    private String rolNombre; // populated from join
    public String getNombreCompleto() {
        return (nombCli != null ? nombCli : "") + " " + (apeCli != null ? apeCli : "");
    }
}