package com.tallerwebi.dominio.utilidad;

import com.tallerwebi.dominio.excepcion.CredencialesInvalidasExcepcion;

public class ValidarString {

    public ValidarString(){

    }

    public Boolean tieneNumeros(String texto) {
        char[] textoArray = texto.toCharArray();
        for(char i : textoArray){
            if(Character.isDigit(i)){
                return true;
            }
        }

        return false;
    }
}
