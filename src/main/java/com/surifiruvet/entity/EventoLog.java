package com.surifiruvet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evento_log")
public class EventoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Evento general
    @Column(name = "tipo_evento", nullable = false, length = 100)
    private String tipoEvento;

    @Column(name = "modulo", length = 100)
    private String modulo;

    @Column(name = "accion", length = 100)
    private String accion;

    @Column(name = "entidad", length = 100)
    private String entidad;

    @Column(name = "id_registro")
    private Long idRegistro;

    @Column(name = "uid", length = 255)
    private String uid;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    // Datos específicos de cita
    @Column(name = "id_cita")
    private Long idCita;

    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "id_mascota")
    private Long idMascota;

    @Column(name = "id_clinica")
    private Long idClinica;

    @Column(name = "nombre_cliente", length = 255)
    private String nombreCliente;

    @Column(name = "nombre_mascota", length = 255)
    private String nombreMascota;

    @Column(name = "nombre_clinica", length = 255)
    private String nombreClinica;

    @Column(name = "nombre_tipo_cita", length = 255)
    private String nombreTipoCita;

    // JSON del evento
    @Column(name = "datos_json", columnDefinition = "TEXT")
    private String datosJson;

    @Column(name = "mensaje_json", nullable = false, columnDefinition = "TEXT")
    private String mensajeJson;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @PrePersist
    public void prePersist() {
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public Long getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(Long idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getIdCita() {
        return idCita;
    }

    public void setIdCita(Long idCita) {
        this.idCita = idCita;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Long getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(Long idMascota) {
        this.idMascota = idMascota;
    }

    public Long getIdClinica() {
        return idClinica;
    }

    public void setIdClinica(Long idClinica) {
        this.idClinica = idClinica;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getNombreClinica() {
        return nombreClinica;
    }

    public void setNombreClinica(String nombreClinica) {
        this.nombreClinica = nombreClinica;
    }

    public String getNombreTipoCita() {
        return nombreTipoCita;
    }

    public void setNombreTipoCita(String nombreTipoCita) {
        this.nombreTipoCita = nombreTipoCita;
    }

    public String getDatosJson() {
        return datosJson;
    }

    public void setDatosJson(String datosJson) {
        this.datosJson = datosJson;
    }

    public String getMensajeJson() {
        return mensajeJson;
    }

    public void setMensajeJson(String mensajeJson) {
        this.mensajeJson = mensajeJson;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}