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
    public String getNombreCompleto() {
        return (nombCli != null ? nombCli : "") + " " + (apeCli != null ? apeCli : "");
    }
}