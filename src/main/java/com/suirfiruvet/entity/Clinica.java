package com.suirfiruvet.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "clinica")
public class Clinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String direccion;
}
