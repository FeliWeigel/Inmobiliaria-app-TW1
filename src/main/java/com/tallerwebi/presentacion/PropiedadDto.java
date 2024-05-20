package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.utilidad.EstadoPropiedad;

public class PropiedadDto {
    private Long id;
    private String nombre;
    private Integer pisos;
    private Integer banios;
    private Integer habitaciones;
    private Double superficie;
    private Double precio;
    private String ubicacion;
    private EstadoPropiedad estado;
    private String rutaImagen;
    private Boolean isFavorite;

    public PropiedadDto(Long id, String nombre, Integer pisos, Integer banios, Integer habitaciones,
                        Double superficie, Double precio, String ubicacion, EstadoPropiedad estado,
                        String rutaImagen, Boolean isFavorite) {
        this.id = id;
        this.nombre = nombre;
        this.pisos = pisos;
        this.banios = banios;
        this.habitaciones = habitaciones;
        this.superficie = superficie;
        this.precio = precio;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.rutaImagen = rutaImagen;
        this.isFavorite = isFavorite;
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

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean favorite) {
        isFavorite = favorite;
    }
}
