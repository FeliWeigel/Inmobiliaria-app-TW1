package com.tallerwebi.presentacion;

public class DatosFiltro {
    private final TipoDeFiltro tipoDeFiltro;
    public Double precio;
    private FiltrarPorPrecio filtroPorPrecio;

    public DatosFiltro(TipoDeFiltro tipoDeFiltro) {
        this.tipoDeFiltro = tipoDeFiltro;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getPrecio(){return this.precio;}

    public void setFiltroPorPrecio(FiltrarPorPrecio filtroPorPrecio) {
        this.filtroPorPrecio = filtroPorPrecio;
    }

    public TipoDeFiltro getTipoDeFiltro() {
        return tipoDeFiltro;
    }

    public FiltrarPorPrecio getFiltroPorPrecio() {
        return filtroPorPrecio;
    }
}
