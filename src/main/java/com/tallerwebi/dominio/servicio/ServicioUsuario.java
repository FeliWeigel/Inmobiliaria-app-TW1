package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.dominio.respositorio.RepositorioUsuario;
import com.tallerwebi.infraestructura.RepositorioHistorialImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ServicioUsuario {

    private final RepositorioUsuario repositorioUsuario;

    /* Se agrega el servicio email con autowired*/
    @Autowired
    private EmailServiceImpl emailService;

    public ServicioUsuario (RepositorioUsuario repositorioUsuario ) {
        this.repositorioUsuario = repositorioUsuario;
    }

    public Usuario buscarPorId(Long id) throws UsuarioInexistenteExcepcion {
        Usuario usuario = repositorioUsuario.buscarPorId(id);

        if (usuario != null){
            return usuario;
        } else {
            throw new UsuarioInexistenteExcepcion();
        }
    }

    public void agregarFavorito(Usuario usuarioAutenticado, Long propiedadId) {
        repositorioUsuario.agregarFavorito(usuarioAutenticado, propiedadId);
    }

    public void eliminarFavorito(Usuario usuarioAutenticado, Long propiedadId) {
        repositorioUsuario.eliminarFavorito(usuarioAutenticado, propiedadId);
    }

    public Set<Propiedad> listarFavoritos(Usuario usuario) {
        return repositorioUsuario.listarFavoritos(usuario);
    }

    public void bloquearUsuario(Long usuarioId) {

        // Se busca al usuario para poder obtener su email
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);

        // Se asigna el mail al que se enviara el mensaje, el asunto y el mensaje.
        String email = usuario.getEmail();
        String subject = "Cuenta Bloqueada";
        String text = "Su cuenta ha sido bloqueada debido a una infracción de las normas del sitio." +
                "\n Comuniquese con un administrador para obtener más detalles al respecto.";

        // se envia el mail y luego se bloquea al usuario
        emailService.sendSimpleMessage(email, subject, text);
        repositorioUsuario.bloquearUsuario(usuarioId);

        // estos pasos se pueden repetir en cualquier función.
        // no olvidarse de agregar ServicioMailImpl con @Autowired en caso de implementarlo en otra clase
    }


    public void eliminarUsuario(Long usuarioId) {
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        String email = usuario.getEmail();
        String subject = "Cuenta Eliminada";
        String text = "Su cuenta ha sido eliminada." +
                "\n Comuniquese con un administrador para obtener más detalles al respecto.";

        try {

            repositorioUsuario.eliminarVisitasPorUsuarioId(usuarioId);
            repositorioUsuario.eliminarUsuario(usuarioId);
        } catch (AlquilerRegistradoException e) {
            throw e;
        }
        emailService.sendSimpleMessage(email, subject, text);
    }


    public void desbloquearUsuario(Long usuarioId) {
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);

        String email = usuario.getEmail();
        String subject = "Cuenta Desbloqueada";
        String text = "Su cuenta ha sido desbloqueada.";

        emailService.sendSimpleMessage(email, subject, text);
        repositorioUsuario.desbloquearUsuario(usuarioId);
    }

    public List<Usuario> listarUsuariosBloqueados() {
        return repositorioUsuario.listarUsuariosBloqueados();
    }

    public List<Usuario> listarUsuariosDesbloqueados() {
        return repositorioUsuario.listarUsuariosDesbloqueados();
    }

    public void editarPerfil(Usuario usuario) throws CredencialesInvalidasExcepcion, PasswordInvalidaExcepcion, UsuarioExistenteExcepcion {
        repositorioUsuario.editarPerfil(usuario);
    }
}
