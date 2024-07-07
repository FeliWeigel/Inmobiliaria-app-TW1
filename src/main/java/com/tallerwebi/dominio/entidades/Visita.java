package com.tallerwebi.dominio.entidades;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class Visita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuarioId", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "propiedadId", nullable = false)
    private Propiedad propiedad;

    private Timestamp fechaVisita;

    public Timestamp getFechaVisita() {
        return fechaVisita;
    }

    public void setFechaVisita(Timestamp fechaVisita) {
        this.fechaVisita = fechaVisita;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Propiedad getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(Propiedad propiedad) {
        this.propiedad = propiedad;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
