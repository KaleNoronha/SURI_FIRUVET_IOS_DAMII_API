package com.surifiruvet.entity;

import jakarta.persistence.*;

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

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombMas() {
        return nombMas;
    }

    public void setNombMas(String nombMas) {
        this.nombMas = nombMas;
    }

    public TipoMascota getTipoMascota() {
        return tipoMascota;
    }

    public void setTipoMascota(TipoMascota tipoMascota) {
        this.tipoMascota = tipoMascota;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getApodos() {
        return apodos;
    }

    public void setApodos(String apodos) {
        this.apodos = apodos;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }
}
