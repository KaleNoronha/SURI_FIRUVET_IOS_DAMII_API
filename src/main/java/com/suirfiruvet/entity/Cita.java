package com.suirfiruvet.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcita")
    private Long idCita;

    @ManyToOne
    @JoinColumn(name = "tipocita")
    private TipoCita tipoCita;

    private LocalDateTime fecha;
    private String comentario;

    @ManyToOne
    @JoinColumn(name = "idmascota")
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "idcliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "idclinica")
    private Clinica clinica;
}
