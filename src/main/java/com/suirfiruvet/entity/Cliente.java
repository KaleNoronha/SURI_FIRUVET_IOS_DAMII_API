package com.suirfiruvet.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombcli")
    private String nombCli;

    @Column(name = "apecli")
    private String apeCli;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "fecnac")
    private LocalDate fecNac;

    private String uid;
}
