package com.tallerwebi.dominio.excepcion;

public class UsuarioExistenteExcepcion extends Exception {
    public UsuarioExistenteExcepcion(String message){
        super(message);
    }
}

