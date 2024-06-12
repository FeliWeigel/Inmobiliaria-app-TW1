package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.AlquilerPropiedad;
import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.RepositorioAlquiler;
import com.tallerwebi.dominio.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;

@Repository("RepositorioAlquiler")
@Transactional
public class RepositorioAlquilerImpl implements RepositorioAlquiler {

    private final SessionFactory sessionFactory;

    public RepositorioAlquilerImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public void nuevoAlquiler(AlquilerPropiedad alquiler) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(alquiler);
    }

    @Override
    public List<AlquilerPropiedad> getAlquileresByPropiedad(Long propiedadId) {
        final Session session = sessionFactory.getCurrentSession();
        String query = "FROM AlquilerPropiedad WHERE propiedad.id = :propiedadId";
        return session.createQuery(query, AlquilerPropiedad.class)
                .setParameter("propiedadId", propiedadId)
                .getResultList();
    }

    @Override
    public List<AlquilerPropiedad> getAlquileresByUsuario(Long usuarioId) {
        final Session session = sessionFactory.getCurrentSession();
        String query = "FROM AlquilerPropiedad WHERE usuario = :usuarioId";
        return session.createQuery(query, AlquilerPropiedad.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }
}
