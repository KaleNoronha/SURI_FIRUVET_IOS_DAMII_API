package com.suirfiruvet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ClienteRequest {
    private String nombCli;
    private String apeCli;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fecNac;
    private String uid;
}
