package com.tallerwebi.dominio;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioPropiedadImpl implements ServicioPropiedad{

    private RepositorioPropiedad repositorioPropiedad;

    public ServicioPropiedadImpl(RepositorioPropiedad repositorioPropiedad) {
        this.repositorioPropiedad = repositorioPropiedad;
    }


    @Override
    public Propiedad buscarPropiedad(Long id) {
        return this.repositorioPropiedad.buscarPropiedad(id);
    }

    @Override
    public List<Propiedad> listarPropiedades() {
        return this.repositorioPropiedad.listarPropiedades();
    }
}
