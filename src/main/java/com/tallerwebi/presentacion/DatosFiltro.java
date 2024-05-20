package com.tallerwebi.presentacion;

public class DatosFiltro {
    private TipoDeFiltro tipoDeFiltro;
    public Double precio;
    private FiltrarPorPrecio filtrarPorPrecio;


    public DatosFiltro() {
    }

    public DatosFiltro(Double precio, FiltrarPorPrecio filtroPrecio) {
        this.precio = precio;
        this.filtrarPorPrecio = filtroPrecio;
    }


    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getPrecio() {
        return this.precio;
    }

    public void setFiltrarPorPrecio(FiltrarPorPrecio filtrarPorPrecio) {
        this.filtrarPorPrecio = filtrarPorPrecio;
    }

    public FiltrarPorPrecio getFiltrarPorPrecio() {
        return filtrarPorPrecio;
    }

    public void setTipoDeFiltro(TipoDeFiltro tipoDeFiltro) {
        this.tipoDeFiltro = tipoDeFiltro;
    }

    public TipoDeFiltro getTipoDeFiltro() {
        return tipoDeFiltro;
    }
}

