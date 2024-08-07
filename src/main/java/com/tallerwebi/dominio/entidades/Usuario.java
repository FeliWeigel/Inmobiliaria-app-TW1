package com.tallerwebi.dominio.entidades;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private Date fechaNacimiento;
    private String email;
    private String password;
    private String rol;
    private Boolean activo = false;
    private String fotoPerfil;

    @OneToMany(
            mappedBy = "usuario", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER
    )
    @Fetch(FetchMode.SUBSELECT)
    private List<AlquilerPropiedad> alquileres;

    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_favoritos",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "propiedad_id")
    )
    private Set<Propiedad> favoritos;

    @OneToMany(
            mappedBy = "usuario", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER
    )
    @Fetch(FetchMode.SUBSELECT) // indicarle a hibernate que obtenga las colecciones en subconsultas
    private List<CalificacionPropiedad> calificaciones;

    @PreRemove
    private void quitarUsuarioDePropiedadFav() {
        List<Propiedad> propiedades = new ArrayList<>(favoritos);
        for (Propiedad propiedad : propiedades) {
            propiedad.removeUsuarioFavorito(this);
        }
    }


    public Usuario() {
    }

    public Usuario(Long id, String nombre, String password) {
        this.id = id;
        this. nombre = nombre;
        this.password = password;
    }

    public void removeFavorito(Propiedad propiedad) {
        favoritos.remove(propiedad);
        propiedad.getUsuariosFavoritos().remove(this);
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRol() {
        return rol;
    }
    public void setRol(String rol) {
        this.rol = rol;
    }
    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Set<Propiedad> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(Set<Propiedad> favoritos) {
        this.favoritos = favoritos;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public List<AlquilerPropiedad> getAlquileres() {
        return alquileres;
    }

    public void setAlquileres(List<AlquilerPropiedad> alquileres) {
        this.alquileres = alquileres;
    }

    public void setVisitas(ArrayList<Object> objects) {
    }

    public List<CalificacionPropiedad> getCalificaciones() {
        return calificaciones;
    }

    public void setCalificaciones(List<CalificacionPropiedad> calificaciones) {
        this.calificaciones = calificaciones;
    }
}
