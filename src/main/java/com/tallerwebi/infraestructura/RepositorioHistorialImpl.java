package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.Visita;
import com.tallerwebi.dominio.respositorio.RepositorioHistorial;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository("RepositorioHistorial")
@Transactional
public class RepositorioHistorialImpl implements RepositorioHistorial {
    private final SessionFactory sessionFactory;
    private final RepositorioHistorial repositorioHistorial;

    @Autowired
    @Lazy
    public RepositorioHistorialImpl(SessionFactory sessionFactory, RepositorioHistorial repositorioHistorial){
        this.sessionFactory = sessionFactory;
        this.repositorioHistorial = repositorioHistorial;
    }

    @Override
    public List<Visita> buscarPorUsuarioId(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        String query = "FROM Visita WHERE usuario.id = :id ORDER BY fechaVisita DESC";
        return session.createQuery(query, Visita.class)
                .setParameter("id", id)
                .setMaxResults(9)
                .getResultList();
    }

    @Override
    public void agregarVisita(Visita visita) {
        sessionFactory.getCurrentSession().save(visita);
    }
}
