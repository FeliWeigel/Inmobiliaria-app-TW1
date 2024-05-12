package com.tallerwebi.presentacion;

public class DatosFiltro {
    private final TipoDeFiltro tipoDeFiltro;
    public Double precio;
    private FiltroPorPrecio filtroPorPrecio;

    public DatosFiltro(TipoDeFiltro tipoDeFiltro) {
        this.tipoDeFiltro = tipoDeFiltro;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getPrecio(){return this.precio;}

    public void setFiltroPorPrecio(FiltroPorPrecio filtroPorPrecio) {
        this.filtroPorPrecio = filtroPorPrecio;
    }

    public TipoDeFiltro getTipoDeFiltro() {
        return tipoDeFiltro;
    }

    public FiltroPorPrecio getFiltroPorPrecio() {
        return filtroPorPrecio;
    }
}
