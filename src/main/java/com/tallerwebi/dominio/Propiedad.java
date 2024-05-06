package com.tallerwebi.dominio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
    private String ubicacion;

    public Propiedad(Long id, String nombre, Integer pisos, Integer banios, Integer habitaciones, Double superficie, Double precio, String ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.pisos = pisos;
        this.banios = banios;
        this.habitaciones = habitaciones;
        this.superficie = superficie;
        this.precio = precio;
        this.ubicacion = ubicacion;
    }

    public Propiedad() {

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

    public void setBanios(Integer baños) {
        this.banios = baños;
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

}
