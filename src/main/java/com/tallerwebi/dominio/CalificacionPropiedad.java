package com.tallerwebi.dominio;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class CalificacionPropiedad {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date fecha;
    private String descripcion;
    private Double puntaje;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuarioId", nullable = false)
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "propiedadId", nullable = false)
    private Propiedad propiedad;

    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Double getPuntaje() {
        return puntaje;
    }
    public void setPuntaje(Double puntaje) {
        this.puntaje = puntaje;
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
}