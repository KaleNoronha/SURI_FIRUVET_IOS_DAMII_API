package com.surifiruvet.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    
 //NUEVO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_anterior_id")
    @JsonBackReference
    private Cita citaAnterior;

    //NUEVO
    @OneToMany(mappedBy = "citaAnterior", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Cita> citasSeguimiento = new ArrayList<>();
}
