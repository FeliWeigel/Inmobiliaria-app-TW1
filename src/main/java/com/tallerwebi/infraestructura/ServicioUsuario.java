package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.RepositorioPropiedad;
import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ServicioUsuario  {

    private RepositorioUsuario repositorioUsuario;
    private RepositorioPropiedad repositorioPropiedad;
    public ServicioUsuario(RepositorioUsuario repositorioUsuario, RepositorioPropiedad repositorioPropiedad) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioPropiedad = repositorioPropiedad;
    }


    public void agregarFavorito(long usuarioId, long propiedadId) {
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        Propiedad propiedad = repositorioPropiedad.buscarPorId(propiedadId);
        repositorioUsuario.agregarFavorito(usuario, propiedad);
    }

    public void eliminarFavorito(long usuarioId, long propiedadId) {
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);
        Propiedad propiedad = repositorioPropiedad.buscarPorId(propiedadId);
        repositorioUsuario.eliminarFavorito(usuario, propiedad);
    }

    public Usuario getUsuarioByEmail(String email) {
        return repositorioUsuario.buscarPorEmail(email);
    }

    //Chat GPT dice:
//    public void agregarAFavoritos(Long usuarioId, Long propiedadId) {
//        Usuario usuario = repositorioUsuario.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
//        Propiedad propiedad = repositorioPropiedad.findById(propiedadId).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
//        usuario.getFavoritos().add(propiedad);
//        repositorioUsuario.save(usuario);
//    }
//
//    public void eliminarDeFavoritos(Long usuarioId, Long propiedadId) {
//        Usuario usuario = repositorioUsuario.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
//        Propiedad propiedad = repositorioPropiedad.findById(propiedadId).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
//        usuario.getFavoritos().remove(propiedad);
//        repositorioUsuario.save(usuario);
//    }

}
