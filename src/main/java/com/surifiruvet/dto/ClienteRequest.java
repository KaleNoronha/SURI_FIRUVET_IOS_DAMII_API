package com.surifiruvet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO de entrada para crear o modificar un cliente.
 * Los campos anotados con @NotBlank/@NotNull son obligatorios.
 * El resource valida con @Valid antes de ejecutar la lógica de negocio.
 */
@Data
public class ClienteRequest {

    @NotBlank(message = "El campo nombCli es requerido.")
    @Size(max = 25, message = "nombCli no puede superar 25 caracteres.")
    private String nombCli;

    @NotBlank(message = "El campo apeCli es requerido.")
    @Size(max = 25, message = "apeCli no puede superar 25 caracteres.")
    private String apeCli;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecNac; // opcional

    @NotBlank(message = "El campo uid es requerido.")
    @Size(max = 128, message = "uid no puede superar 128 caracteres.")
    private String uid;

    @NotNull(message = "El campo idRol es requerido.")
    private Long idRol; // 1 = usuario, 2 = administrador
}
