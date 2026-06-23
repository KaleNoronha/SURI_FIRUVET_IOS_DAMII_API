package com.surifiruvet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "evento_log")
public class EventoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "datos_json", columnDefinition = "TEXT")
    private String datosJson;

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
}
