package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.filtro.FiltroPropiedad;
import com.tallerwebi.presentacion.DatosFiltro;
import com.tallerwebi.presentacion.FiltrarPorPrecio;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioPropiedad {

    private RepositorioPropiedad repositorioPropiedad;

    public ServicioPropiedad(RepositorioPropiedad repositorioPropiedad) {
        this.repositorioPropiedad = repositorioPropiedad;
    }

    // Para probar que funcionen las views, descomentar las lineas comentadas y comentar las que estan encima de ellas.

    public Propiedad buscarPropiedad(Long id) {

        //Propiedad propiedad = this.repositorioPropiedad.buscarPropiedad(id);
        Propiedad propiedad = busquedaFalsa(id, propiedadesFalsas());

        if (propiedad != null) {
            return propiedad;
        }
        else throw new CRUDPropiedadExcepcion("La Propiedad Buscada no Existe.");

    }


    public List<Propiedad> listarPropiedades() {
        //return this.repositorioPropiedad.listarPropiedades();
        return propiedadesFalsas();
    }


    public List<Propiedad> filtrar(FiltroPropiedad filtro, DatosFiltro datosFiltro) {
        return filtro.filtrar(listarPropiedades(), datosFiltro);
    }




    //                              BORRAR MÁS TARDE:
    // Estos metodos son solo para probar que las vistas anden bien, eliminarlos cuando haya una base de datos ya creada.

    private List<Propiedad> propiedadesFalsas() {

        List<Propiedad> propiedades = new ArrayList<>();

        Propiedad propiedad1 = new Propiedad(1L, "Casa 1", 2, 3, 4,
                150.0, 110000.0, "Ubicacion 1");
        Propiedad propiedad2 = new Propiedad(2L, "Casa 2", 2, 3, 4,
                200.0, 120000.0, "Ubicacion 2");
        Propiedad propiedad3 = new Propiedad(3L, "Casa 3", 2, 3, 4,
                250.0, 150000.0, "Ubicacion 3");
        Propiedad propiedad4 = new Propiedad(4L, "Casa 4", 2, 3, 4,
                100.0, 10000.0, "Ubicacion 4");
        Propiedad propiedad5 = new Propiedad(5L, "Casa 5", 2, 3, 4,
                50.0, 1200.0, "Ubicacion 5");
        Propiedad propiedad6 = new Propiedad(6L, "Casa 6", 2, 3, 4,
                80.0, 1500.0, "Ubicacion 6");
        Propiedad propiedad7 = new Propiedad(7L, "Casa 7", 2, 3, 4,
                100.0, 10000.0, "Ubicacion 7");
        Propiedad propiedad8 = new Propiedad(8L, "Casa 8", 2, 3, 4,
                50.0, 1200.0, "Ubicacion 8");
        Propiedad propiedad9 = new Propiedad(9L, "Casa 9", 2, 3, 4,
                80.0, 1500.0, "Ubicacion 9");


        propiedades.add(propiedad1);
        propiedades.add(propiedad2);
        propiedades.add(propiedad3);
        propiedades.add(propiedad4);
        propiedades.add(propiedad5);
        propiedades.add(propiedad6);
        propiedades.add(propiedad7);
        propiedades.add(propiedad8);
        propiedades.add(propiedad9);

        return propiedades;
    }

    private Propiedad busquedaFalsa(Long id, List<Propiedad> propiedades ) {

        for (Propiedad propiedad : propiedades) {
            if (propiedad.getId().equals(id)) {
                return propiedad;
            }
        }
        return null;
    }

}
