package com.tallerwebi.dominio.excepcion;

public class CredencialesInvalidasExcepcion extends RuntimeException{
    public CredencialesInvalidasExcepcion(String message){
        super(message);
    }
}
