package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.RepositorioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CredencialesInvalidasExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioExistenteExcepcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("repositorioLogin")
@Transactional
public class ServicioLoginImpl implements RepositorioLogin {

    private RepositorioUsuario repositorioUsuario;

    @Autowired
    public ServicioLoginImpl(RepositorioUsuario repositorioUsuario){
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public Usuario consultarUsuario (String email, String password) {
        return repositorioUsuario.buscarUsuario(email, password);
    }

    @Override
    public void registrar(Usuario usuario) throws UsuarioExistenteExcepcion {
        if(usuario.getEmail() == null || usuario.getPassword() == null || usuario.getNombre() == null
                || usuario.getApellido() == null || usuario.getFechaNacimiento() == null){
            throw new CredencialesInvalidasExcepcion("Error! Debe completar todos los campos.");
        }
        validarNombreApellido(usuario.getNombre());
        validarNombreApellido(usuario.getApellido());
        validarEdad(usuario.getFechaNacimiento());
        if(usuario.getPassword().length() >= 6){
            if(!validarPassword(usuario.getPassword())){
                throw new CredencialesInvalidasExcepcion(
                        "Error! La contraseña debe contener al menos: un numero, una mayuscula y un caracter especial."
                );
            }
        }else {
            throw new CredencialesInvalidasExcepcion(
                    "Error! La contraseña debe contener al menos 6 digitos de longitud."
            );
        }
        if(repositorioUsuario.buscarPorEmail(usuario.getEmail()) != null){
            throw new UsuarioExistenteExcepcion("El email ingresado ya esta asociado a una cuenta existente.");
        }

        repositorioUsuario.guardar(usuario);
    }

    // Valida que el nombre del usuario no contenga numeros
    private void validarNombreApellido(String nombreUsuario){
        char[] nombreArray = nombreUsuario.toCharArray();
        for(char i : nombreArray){
            if(Character.isDigit(i)){
                throw new CredencialesInvalidasExcepcion("Ingrese su nombre y apellido correctamente!");
            }
        }
    }
    // Valida que la edad del usuario sea exactamente mayor a 18 años
    private void validarEdad(Date edadUsuario){
        if((new Date().getYear() - edadUsuario.getYear()) < 18){
            throw new CredencialesInvalidasExcepcion("Cuidado! Debes ser mayor de 18 años para registrarte.");
        }else if((new Date().getYear() - edadUsuario.getYear()) == 18){
            if(new Date().getMonth() < edadUsuario.getMonth()){
                throw new CredencialesInvalidasExcepcion("Cuidado! Debes ser mayor de 18 años para registrarte.");
            }else if(new Date().getMonth() == edadUsuario.getMonth()){
                if(new Date().getDay() < edadUsuario.getDay()){
                    throw new CredencialesInvalidasExcepcion("Cuidado! Debes ser mayor de 18 años para registrarte.");
                }
            }
        }
    }
    //Valida que la contraseña contenga al menos 6 caracteres, una mayuscula, un numero y un caracter especial
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

