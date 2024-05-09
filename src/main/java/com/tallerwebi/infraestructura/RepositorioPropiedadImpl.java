package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.RepositorioPropiedad;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service("repositorioPropiedad")
@Transactional
public class RepositorioPropiedadImpl implements RepositorioPropiedad {

    private SessionFactory sessionFactory;

    public RepositorioPropiedadImpl(SessionFactory sessionFactory) {

        this.sessionFactory = sessionFactory;
    }

    @Override
    public Propiedad buscarPropiedad(Long id) {
        final Session session = sessionFactory.getCurrentSession();

        Propiedad propiedadBuscada = (Propiedad) session.createCriteria(Propiedad.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        if (propiedadBuscada == null) {
            throw new CRUDPropiedadExcepcion("No se encontro la propiedad buscada.");
        } else {
            return propiedadBuscada;
        }
    }

    @Override
    public Boolean agregarPropiedad(Propiedad propiedad) {
        final Session session = sessionFactory.getCurrentSession();

        if(propiedad.getNombre() != null && propiedad.getBanios() != null && propiedad.getHabitaciones() != null
                && propiedad.getPisos() != null && propiedad.getPrecio() != null && propiedad.getSuperficie() != null && propiedad.getUbicacion() != null){
            session.save(propiedad);
            return true;
        } else {
            throw new CRUDPropiedadExcepcion("Todos los campos deben completarse con datos validos.");
        }

    }

    @Override
    public void editarPropiedad(Propiedad propiedadEditada) {
        final Session session = sessionFactory.getCurrentSession();

        if (propiedadEditada.getId() != null) {
            Propiedad propiedadAlmacenada = buscarPropiedad(propiedadEditada.getId());

            if (propiedadAlmacenada != null) {
                propiedadAlmacenada.setNombre(propiedadEditada.getNombre());
                propiedadAlmacenada.setBanios(propiedadEditada.getBanios());
                propiedadAlmacenada.setHabitaciones(propiedadEditada.getHabitaciones());
                propiedadAlmacenada.setPisos(propiedadEditada.getPisos());
                propiedadAlmacenada.setSuperficie(propiedadEditada.getSuperficie());
                propiedadAlmacenada.setPrecio(propiedadEditada.getPrecio());
                propiedadAlmacenada.setUbicacion(propiedadEditada.getUbicacion());

                session.saveOrUpdate(propiedadAlmacenada);
            } else {
                throw new CRUDPropiedadExcepcion("La propiedad no existe en la base de datos.");
            }
        } else {
            throw new CRUDPropiedadExcepcion("La propiedad no cuenta con ID definido para su búsqueda en la base de datos.");
        }
    }


    @Override
    public void eliminarPropiedad(Long propiedadId) {
        final Session session = sessionFactory.getCurrentSession();
        Propiedad propiedadAEliminar = buscarPropiedad(propiedadId);

        if(propiedadAEliminar != null){
            session.delete(propiedadAEliminar);
        } else {
            throw new CRUDPropiedadExcepcion("La propiedad seleccionda para eliminar no existe en la base de datos.");
        }
    }
    @Override
    public List<Propiedad> listarPropiedades() {
        final Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Propiedad").getResultList();
    }
}
