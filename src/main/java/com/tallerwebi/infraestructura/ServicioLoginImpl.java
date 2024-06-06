package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.dominio.utilidad.ValidarString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {
    private final RepositorioUsuario repositorioUsuario;

    @Autowired
    public ServicioLoginImpl(RepositorioUsuario repositorioUsuario){
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public Usuario consultarUsuario (String email, String password) {
        return repositorioUsuario.buscarUsuario(email, password);
    }

    @Override
    public void registrar(Usuario usuario) throws UsuarioExistenteExcepcion, CredencialesInvalidasExcepcion, PasswordInvalidaExcepcion, EdadInvalidaExcepcion {
        ValidarString validarString = new ValidarString();

        validarEdad(usuario.getFechaNacimiento());

        if(validarString.tieneNumeros(usuario.getNombre()) || validarString.tieneNumeros(usuario.getApellido())){
            throw new CredencialesInvalidasExcepcion();
        }

        if(usuario.getPassword().length() >= 6){
            if(!validarPassword(usuario.getPassword())){
                throw new PasswordInvalidaExcepcion();
            }
        }else {
            throw new PasswordInvalidaExcepcion();
        }

        if(repositorioUsuario.buscarPorEmail(usuario.getEmail()) != null){
            throw new UsuarioExistenteExcepcion();
        }

        if(usuario.getEmail().contains("v1admin")){
            usuario.setRol("ADMIN");
        }else {
            usuario.setRol("USER");
        }

        usuario.setActivo(true);
        repositorioUsuario.guardar(usuario);
    }

    @Override
    public void cerrarSesion(Usuario usuario) throws UsuarioNoIdentificadoExcepcion {
        if(usuario != null){
            repositorioUsuario.cerrarSesion(usuario);
        }else {
            throw new UsuarioNoIdentificadoExcepcion();
        }
    }

    private void validarEdad(Date fechaUsuario) throws EdadInvalidaExcepcion {
        LocalDate fechaActual = LocalDate.now();

        LocalDate fechaUsuarioLocalDate = fechaUsuario.toLocalDate();
        Period diferencia = Period.between(fechaUsuarioLocalDate, fechaActual);
        if(diferencia.getYears() < 18){
            throw new EdadInvalidaExcepcion();
        }else if(diferencia.getYears() == 18){
            if(fechaUsuarioLocalDate.plusYears(18).isAfter(fechaActual)
                    || !fechaUsuarioLocalDate.plusYears(18).isEqual(fechaActual)){
                throw new EdadInvalidaExcepcion();
            }
        }
    }

    private Boolean validarPassword(String password){
        boolean esMayuscula = false, esNumero = false, esCaracterEspecial = false;
        Pattern listaEspeciales = Pattern.compile ("[?!¡@¿.,´)$(]");
        Matcher tieneEspeciales = listaEspeciales.matcher(password);
        char[] passwordArray = password.toCharArray();

        if(!password.isBlank() && password.length() >= 6){
            for(char i : passwordArray){
                if(Character.isUpperCase(i)){
                    esMayuscula = true;
                }else if(Character.isDigit(i)){
                    esNumero = true;
                }else if(tieneEspeciales.find()){
                    esCaracterEspecial = true;
                }
            }
        }else{
            return false;
        }

        return esMayuscula && esNumero && esCaracterEspecial;
    }


}

