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
        repositorioUsuario.agregarFavorito(propiedad);
    }

    public void eliminarFavorito(Usuario usuario, Propiedad propiedad) {
        repositorioUsuario.eliminarFavorito(propiedad);
    }
}
