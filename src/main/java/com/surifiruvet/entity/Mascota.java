package com.surifiruvet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    @Column(name = "apodos")
    private String apodos;

    @Column(name = "alergias")
    private String alergias;
}
