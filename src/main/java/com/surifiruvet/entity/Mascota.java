package com.surifiruvet.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;

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
    
 // NUEVO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "padre_id")
    @JsonBackReference // Evita bucle infinito JSON (hijo -> padre)
    private Mascota padre;

    // NUEVO
    @OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Permite serializar hijos (padre -> hijos)
    private List<Mascota> hijos = new ArrayList<>();
}
