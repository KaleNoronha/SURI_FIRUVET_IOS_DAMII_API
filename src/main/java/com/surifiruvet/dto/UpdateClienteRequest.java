package com.surifiruvet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO para actualizar datos de un cliente.
 * No incluye uid (inmutable) ni idRol (endpoint separado PATCH /rol).
 */
@Data
public class UpdateClienteRequest {

    @Size(max = 25, message = "nombCli no puede superar 25 caracteres.")
    private String nombCli;

    @Size(max = 25, message = "apeCli no puede superar 25 caracteres.")
    private String apeCli;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fecNac;
}
