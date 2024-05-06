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

    @Override
    public Propiedad buscarPropiedad(Long id) {
        final Session session = sessionFactory.getCurrentSession();

        return (Propiedad) session.createCriteria(Propiedad.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public Propiedad agregarPropiedad(Propiedad propiedad) {
        final Session session = sessionFactory.getCurrentSession();

        if(propiedad.getNombre() != null && propiedad.getBanios() != null && propiedad.getHabitaciones() != null
                && propiedad.getPisos() != null && propiedad.getPrecio() != null && propiedad.getSuperficie() != null && propiedad.getUbicacion() != null){
            return (Propiedad) session.save(propiedad);
        }

        throw new CRUDPropiedadExcepcion("Todos los campos deben completarse con datos validos.");
    }
    @Override
    public Propiedad editarPropiedad(Propiedad propiedadEditada) {
        final Session session = sessionFactory.getCurrentSession();
        if(propiedadEditada.getId() != null){
            Propiedad propiedadAlmacenada = buscarPropiedad(propiedadEditada.getId());

            if(propiedadAlmacenada != null){
                return (Propiedad) session.save(propiedadAlmacenada);
            }
        }
        throw new CRUDPropiedadExcepcion("La propiedad no cuenta con ID definido para su busqueda en la base de datos.");
    }
    @Override
    public void eliminarPropiedad(Long propiedadId) {
        final Session session = sessionFactory.getCurrentSession();
        Propiedad propiedadAEliminar = buscarPropiedad(propiedadId);

        if(propiedadAEliminar != null){
            session.delete(propiedadAEliminar);
        }
        throw new CRUDPropiedadExcepcion("La propiedad seleccionda para eliminar no existe en la base de datos.");
    }
    @Override
    public List<Propiedad> listarPropiedades() {
        final Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Propiedad").getResultList();
    }
}
