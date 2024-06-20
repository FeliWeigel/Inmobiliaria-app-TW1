package com.tallerwebi.dominio;

import com.tallerwebi.dominio.utilidad.EstadoPropiedad;

public class FiltroPropiedad {
    private Double minPrecio;
    private Double maxPrecio;
    private String ubicacion;
    private EstadoPropiedad estado;
    private Double superficie;

    public Double getSuperficie() {
        return superficie;
    }
    public void setSuperficie(Double superficie) {
        this.superficie = superficie;
    }
    public Double getMinPrecio() { return minPrecio; }
    public void setMinPrecio(Double minPrecio) { this.minPrecio = minPrecio; }
    public Double getMaxPrecio() { return maxPrecio; }
    public void setMaxPrecio(Double maxPrecio) { this.maxPrecio = maxPrecio; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public EstadoPropiedad getEstado() {
        return estado;
    }
    public void setEstado(EstadoPropiedad estado) {
        this.estado = estado;
    }
}
