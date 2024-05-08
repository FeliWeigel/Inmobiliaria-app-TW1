package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.RepositorioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CredencialesInvalidasExcepcion;
import com.tallerwebi.dominio.excepcion.EdadInvalidaExcepcion;
import com.tallerwebi.dominio.excepcion.PasswordInvalidaExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioExistenteExcepcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("repositorioLogin")
@Transactional
public class RepositorioLoginImpl implements RepositorioLogin {
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    public RepositorioLoginImpl(RepositorioUsuario repositorioUsuario){
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public Usuario consultarUsuario (String email, String password) {
        return repositorioUsuario.buscarUsuario(email, password);
    }

    @Override
    public void registrar(Usuario usuario) throws UsuarioExistenteExcepcion, CredencialesInvalidasExcepcion, PasswordInvalidaExcepcion, EdadInvalidaExcepcion {
        if(usuario.getEmail() == null || usuario.getPassword() == null || usuario.getNombre() == null
                || usuario.getApellido() == null || usuario.getFechaNacimiento() == null){
            throw new CredencialesInvalidasExcepcion();
        }
        validarNombreApellido(usuario.getNombre());
        validarNombreApellido(usuario.getApellido());
        validarEdad(usuario.getFechaNacimiento());

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

        repositorioUsuario.guardar(usuario);
    }

    private void validarNombreApellido(String nombreUsuario) throws CredencialesInvalidasExcepcion {
        char[] nombreArray = nombreUsuario.toCharArray();
        for(char i : nombreArray){
            if(Character.isDigit(i)){
                throw new CredencialesInvalidasExcepcion();
            }
        }
    }

    private void validarEdad(Date edadUsuario) throws EdadInvalidaExcepcion {
        if((new Date().getYear() - edadUsuario.getYear()) < 18){
            throw new EdadInvalidaExcepcion();
        }else if((new Date().getYear() - edadUsuario.getYear()) == 18){
            if(new Date().getMonth() < edadUsuario.getMonth()){
                throw new EdadInvalidaExcepcion();
            }else if(new Date().getMonth() == edadUsuario.getMonth()){
                if(new Date().getDay() < edadUsuario.getDay()){
                    throw new EdadInvalidaExcepcion();
                }
            }
        }
    }

    private Boolean validarPassword(String password){
        boolean esMayuscula = false, esNumero = false, esCaracterEspecial = false;
        Pattern listaEspeciales = Pattern.compile ("[?!¡@¿.,´)]");
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

