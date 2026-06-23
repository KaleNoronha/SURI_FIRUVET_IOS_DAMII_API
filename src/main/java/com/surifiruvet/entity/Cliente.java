package com.surifiruvet.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
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
