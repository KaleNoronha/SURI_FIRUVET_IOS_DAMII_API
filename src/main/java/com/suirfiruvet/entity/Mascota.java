package com.suirfiruvet.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "mascota")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombmas")
    private String nombMas;

    @ManyToOne
    @JoinColumn(name = "tipomas")
    private TipoMascota tipoMascota;

    @ManyToOne
    @JoinColumn(name = "idcliente")
    private Cliente cliente;
}
