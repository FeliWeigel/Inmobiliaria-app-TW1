package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.CalificacionPropiedad;
import com.tallerwebi.dominio.respositorio.RepositorioCalificacion;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository("RepositorioCalificacion")
@Transactional
public class RepositorioCalificacionImpl implements RepositorioCalificacion {

    private final SessionFactory sessionFactory;

    public RepositorioCalificacionImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<CalificacionPropiedad> listarCalificacionesPorPropiedad(Long propiedadId) {
        final Session session = sessionFactory.getCurrentSession();
        String query = "FROM CalificacionPropiedad WHERE propiedad.id = :propiedadId";
        return session.createQuery(query, CalificacionPropiedad.class)
                .setParameter("propiedadId", propiedadId)
                .getResultList();
    }

    @Override
    public List<CalificacionPropiedad> listarCalificacionesPorUsuario(Long usuarioId) {
        final Session session = sessionFactory.getCurrentSession();
        String query = "FROM CalificacionPropiedad WHERE usuario.id = :usuarioId";
        return session.createQuery(query, CalificacionPropiedad.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    @Override
    public void agregarNuevaCalificacion(CalificacionPropiedad calificacion) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(calificacion);
    }
}
