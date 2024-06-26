package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.entidades.CalificacionPropiedad;
import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.excepcion.CalificacionDenegadaExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioNoIdentificadoExcepcion;
import com.tallerwebi.dominio.respositorio.RepositorioCalificacion;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class ServicioCalificacion {
    private final RepositorioCalificacion repositorioCalificacion;
    private final ServicioPropiedad servicioPropiedad;
    public ServicioCalificacion(RepositorioCalificacion repositorioCalificacion, ServicioPropiedad servicioPropiedad) {
        this.repositorioCalificacion = repositorioCalificacion;
        this.servicioPropiedad = servicioPropiedad;
    }

    public void agregarNuevaCalificacion(Long propiedadId, Usuario usuario, String descripcion, Double puntaje) throws UsuarioNoIdentificadoExcepcion {
        if(usuario == null){
            throw new UsuarioNoIdentificadoExcepcion();
        }
        if(propiedadId == null){
            throw new CRUDPropiedadExcepcion("El id de la propiedad es nulo!");
        }
        if(descripcion == null || puntaje == null){
            throw new CalificacionDenegadaExcepcion("Se deben completar todos los campos para continuar.");
        }
        Propiedad propiedadAlmacenada = servicioPropiedad.buscarPropiedad(propiedadId);
        if(propiedadAlmacenada == null){
            throw new CRUDPropiedadExcepcion("La propiedad no ha sido encontrada.");
        }
        CalificacionPropiedad nuevaCalificacion = new CalificacionPropiedad();
        nuevaCalificacion.setUsuario(usuario);
        nuevaCalificacion.setPropiedad(propiedadAlmacenada);
        nuevaCalificacion.setDescripcion(descripcion);
        nuevaCalificacion.setPuntaje(puntaje);
        nuevaCalificacion.setFecha(new Date(System.currentTimeMillis()));
        repositorioCalificacion.agregarNuevaCalificacion(nuevaCalificacion);
    }

    public List<CalificacionPropiedad> listarCalificacionesPorPropiedad(Long propiedadId){
        Propiedad propiedadAlmacenada = servicioPropiedad.buscarPropiedad(propiedadId);

        if(propiedadAlmacenada == null){
            throw new CRUDPropiedadExcepcion("La propiedad: " + propiedadId + "no ha sido encontrada para mostrar sus calificaciones.");
        }

        return repositorioCalificacion.listarCalificacionesPorPropiedad(propiedadId);
    }

    public CalificacionPropiedad getCalificacion(Long calificacionId){
        if(calificacionId == null){
            throw new CalificacionDenegadaExcepcion("Error! No se ha encontrado la calificacion, el ID es nulo!");
        }
        CalificacionPropiedad calificacion = repositorioCalificacion.getCalificacionPorId(calificacionId);

        if(calificacion == null){
            throw new CalificacionDenegadaExcepcion("La calificacion de id: " + calificacionId + " no ha sido encontrada.");
        }

        return calificacion;
    }
}
