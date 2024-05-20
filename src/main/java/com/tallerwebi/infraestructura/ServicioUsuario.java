package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.RepositorioPropiedad;
import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class ServicioUsuario  {

    private RepositorioUsuario repositorioUsuario;
    public ServicioUsuario(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }


    public void agregarFavorito(Usuario usuario, Propiedad propiedad) {
        repositorioUsuario.agregarFavorito(usuario, propiedad);
    }

    public void eliminarFavorito(Usuario usuario, Propiedad propiedad) {
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
