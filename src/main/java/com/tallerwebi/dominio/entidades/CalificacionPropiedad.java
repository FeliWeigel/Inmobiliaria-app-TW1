package com.tallerwebi.dominio.entidades;

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
    private String respuesta;


    @PreRemove
    private void preRemove() {
        if (usuario != null) {
            usuario.getCalificaciones().remove(this);
        }
        if (propiedad != null) {
            propiedad.getCalificaciones().remove(this);
        }
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getRespuesta() {
        return this.respuesta;
    }
}
