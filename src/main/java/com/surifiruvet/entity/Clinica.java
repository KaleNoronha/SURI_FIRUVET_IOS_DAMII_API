package com.surifiruvet.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "clinica")
public class Clinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String direccion;
    
 // 👇 NUEVO: Relación recursiva - Sede Padre
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_padre_id")
    @JsonBackReference
    private Clinica sedePadre;

    // 👇 NUEVO: Relación recursiva - Sedes Hijas
    @OneToMany(mappedBy = "sedePadre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Clinica> sedesHijas = new ArrayList<>();
}
