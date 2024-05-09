package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Service
public class ServicioPropiedad {

    private RepositorioPropiedad repositorioPropiedad;

    public ServicioPropiedad(RepositorioPropiedad repositorioPropiedad) {

        this.repositorioPropiedad = repositorioPropiedad;
    }


    public Propiedad buscarPropiedad(Long id) {

        Propiedad propiedad = this.repositorioPropiedad.buscarPropiedad(id);

        if (propiedad != null) {
            return propiedad;
        }
        else throw new CRUDPropiedadExcepcion("La Propiedad Buscada no Existe.");

    }


    public List<Propiedad> listarPropiedades() {

        return this.repositorioPropiedad.listarPropiedades();
    }
}
