package com.tallerwebi.dominio;

import com.tallerwebi.dominio.utilidad.EstadoPropiedad;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class Propiedad {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nombre;
    private Integer pisos;
    private Integer banios;
    private Integer habitaciones;
    private Double superficie;
    private Double precio;
    private EstadoPropiedad estado;
    private String ubicacion;
    private String rutaImagen;
    private Boolean aceptada; // Nuevo atributo

    @OneToMany(mappedBy = "propiedad", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AlquilerPropiedad> alquileres;

    public Propiedad(Long id, String nombre, Integer pisos, Integer banios, Integer habitaciones, Double superficie, Double precio, String ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.pisos = pisos;
        this.banios = banios;
        this.habitaciones = habitaciones;
        this.superficie = superficie;
        this.precio = precio;
        this.ubicacion = ubicacion;
        this.aceptada = false;
    }

    @ManyToMany(mappedBy = "favoritos")
    private Set<Usuario> usuariosFavoritos = new HashSet<>();

    @PreRemove
    private void quitarPropiedadDeUsuarios() {
        for (Usuario usuario : usuariosFavoritos) {
            usuario.getFavoritos().remove(this);
        }
    }

    public Propiedad() {
        this.aceptada = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public Integer getPisos() {
        return pisos;
    }
    public void setPisos(Integer pisos) {
        this.pisos = pisos;
    }
    public Integer getBanios() {
        return banios;
    }
    public void setBanios(Integer banios) {
        this.banios = banios;
    }
    public Integer getHabitaciones() {
        return habitaciones;
    }
    public void setHabitaciones(Integer habitaciones) {
        this.habitaciones = habitaciones;
    }
    public Double getSuperficie() {
        return superficie;
    }
    public void setSuperficie(Double superficie) {
        this.superficie = superficie;
    }
    public Double getPrecio() {
        return precio;
    }
    public void setPrecio(Double precio) {
        this.precio = precio;
    }
    public String getUbicacion() {
        return ubicacion;
    }
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    public EstadoPropiedad getEstado() {
        return estado;
    }
    public void setEstado(EstadoPropiedad estado) {
        this.estado = estado;
    }
    public String getRutaImagen() {
        return rutaImagen;
    }
    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public boolean isAceptada() {
        return aceptada;
    }

    public void setAceptada(Boolean aceptada) {
        this.aceptada = aceptada;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Propiedad propiedad = (Propiedad) o;
        return Objects.equals(id, propiedad.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Boolean getAceptada() {
        return aceptada;
    }

    public List<AlquilerPropiedad> getAlquileres() {
        return alquileres;
    }

    public void setAlquileres(List<AlquilerPropiedad> alquileres) {
        this.alquileres = alquileres;
    }

    public Set<Usuario> getUsuariosFavoritos() {
        return usuariosFavoritos;
    }

    public void setUsuariosFavoritos(Set<Usuario> usuariosFavoritos) {
        this.usuariosFavoritos = usuariosFavoritos;
    }
}
