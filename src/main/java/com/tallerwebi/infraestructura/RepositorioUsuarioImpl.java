package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.respositorio.RepositorioPropiedad;
import com.tallerwebi.dominio.respositorio.RepositorioUsuario;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.dominio.utilidad.ValidarString;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository("RepositorioUsuario")
@Transactional
public class RepositorioUsuarioImpl implements RepositorioUsuario {
    private final SessionFactory sessionFactory;
    private final RepositorioPropiedad repositorioPropiedad;

    @Autowired
    public RepositorioUsuarioImpl(SessionFactory sessionFactory, RepositorioPropiedad repositorioPropiedad){
        this.sessionFactory = sessionFactory;
        this.repositorioPropiedad = repositorioPropiedad;
    }

    @Override
    public Usuario buscarUsuario(String email, String password) {
        final Session session = sessionFactory.getCurrentSession();
        return (Usuario) session.createCriteria(Usuario.class)
                .add(Restrictions.eq("email", email))
                .add(Restrictions.eq("password", password))
                .uniqueResult();
    }

    @Override
    public void guardar(Usuario usuario) {
        sessionFactory.getCurrentSession().save(usuario);
    }

    @Override
    public void eliminarUsuario(Long id) throws AlquilerRegistradoException {
        Usuario usuario = this.buscarPorId(id);
        if(usuario!=null && !usuario.getActivo()){

            if (!usuario.getAlquileres().isEmpty()) {
                throw new AlquilerRegistradoException("El usuario no puede eliminarse, tiene alquileres pendientes.");
            } else {
                sessionFactory.getCurrentSession().delete(usuario);
            }
        }
    }

    @Override
    public void eliminarVisitasPorUsuarioId(Long usuarioId) {
        String hql = "DELETE FROM Visita WHERE usuarioId = :usuarioId";
        sessionFactory.getCurrentSession().createQuery(hql)
                .setParameter("usuarioId", usuarioId)
                .executeUpdate();
    }

    @Override
    public void eliminarCalificacionesPorUsuarioId(Long usuarioId) {
        String hql = "DELETE FROM CalificacionPropiedad WHERE usuario.id = :usuarioId";
        sessionFactory.getCurrentSession().createQuery(hql)
                .setParameter("usuarioId", usuarioId)
                .executeUpdate();
    }



    @Override
    public Usuario buscarPorEmail(String email) {
        return (Usuario) sessionFactory.getCurrentSession().createCriteria(Usuario.class)
                .add(Restrictions.eq("email", email))
                .uniqueResult();
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return (Usuario) sessionFactory.getCurrentSession().createCriteria(Usuario.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();
    }

    @Override
    public void editarPerfil(Usuario usuario) throws CredencialesInvalidasExcepcion, PasswordInvalidaExcepcion, UsuarioExistenteExcepcion {
        final Session session = sessionFactory.getCurrentSession();
        Usuario usuarioAlmacenado = session.get(Usuario.class, usuario.getId());
        ValidarString validarString = new ValidarString();

        if(usuarioAlmacenado == null){
            throw new CRUDPropiedadExcepcion("Error! El usuario no pudo ser encontrado.");
        }

        if(validarString.tieneNumeros(usuario.getNombre()) || validarString.tieneNumeros(usuario.getApellido())){
            throw new CredencialesInvalidasExcepcion();
        }

        if(usuario.getPassword().length() >= 6){
            if(!validarPassword(usuario.getPassword())){
                throw new PasswordInvalidaExcepcion();
            }
        }else {
            throw new PasswordInvalidaExcepcion();
        }

        if(!usuarioAlmacenado.getEmail().equals(usuario.getEmail()) && this.buscarPorEmail(usuario.getEmail()) != null){
            throw new UsuarioExistenteExcepcion();
        }

        usuarioAlmacenado.setEmail(usuario.getEmail());
        usuarioAlmacenado.setPassword(usuario.getPassword());
        usuarioAlmacenado.setNombre(usuario.getNombre());
        usuarioAlmacenado.setApellido(usuario.getApellido());
        usuarioAlmacenado.setFotoPerfil(usuario.getFotoPerfil());

        session.update(usuarioAlmacenado);
    }



    @Override
    @Transactional
    public void agregarFavorito(Usuario usuario, Long propiedadId) {
        final Session session = sessionFactory.getCurrentSession();
        Usuario usuarioAlmacenado = session.get(Usuario.class, usuario.getId());
        Propiedad propiedadAlmacenada = repositorioPropiedad.buscarPropiedad(propiedadId);

        if(propiedadAlmacenada == null){
            throw new CRUDPropiedadExcepcion("Error! La propiedad no pudo ser encontrada.");
        }

        if(usuarioAlmacenado.getFavoritos() == null){
            usuarioAlmacenado.setFavoritos(new HashSet<>());
        }

        if (!usuarioAlmacenado.getFavoritos().contains(propiedadAlmacenada)) {
            usuarioAlmacenado.getFavoritos().add(propiedadAlmacenada);
            sessionFactory.getCurrentSession().update(usuarioAlmacenado);
        }else {
            throw new CRUDPropiedadExcepcion("Error! La propiedad forma parte de la lista de favoritos.");
        }
    }

    @Override
    @Transactional
    public void eliminarFavorito(Usuario usuario, Long propiedadId) {
        final Session session = sessionFactory.getCurrentSession();
        Usuario usuarioAlmacenado = session.get(Usuario.class, usuario.getId());
        Propiedad propiedadAlmacenada = repositorioPropiedad.buscarPropiedad(propiedadId);

        if(propiedadAlmacenada == null){
            throw new CRUDPropiedadExcepcion("Error! La propiedad no pudo ser encontrada.");
        }

        if (usuarioAlmacenado.getFavoritos().contains(propiedadAlmacenada)) {
            usuarioAlmacenado.getFavoritos().remove(propiedadAlmacenada);
            sessionFactory.getCurrentSession().update(usuarioAlmacenado);
        } else {
            throw new CRUDPropiedadExcepcion("Error! La propiedad no forma parte de la lista de favoritos.");
        }

    }

    @Override
    @Transactional
    public Set<Propiedad> listarFavoritos(Usuario usuario) {
        final Session session = sessionFactory.getCurrentSession();
        Usuario usuarioAlmacenado = session.get(Usuario.class, usuario.getId());

        if(usuarioAlmacenado != null){
            return usuarioAlmacenado.getFavoritos();
        }

        throw new CRUDPropiedadExcepcion("Error! El usuario no ha sido encontrado.");
    }

    @Override
    public void cerrarSesion(Usuario usuario) throws UsuarioNoIdentificadoExcepcion {
        final Session session = sessionFactory.getCurrentSession();
        Usuario usuarioAlmacenado = session.get(Usuario.class, usuario.getId());
        if(usuarioAlmacenado != null){
            session.clear();
        }else {
            throw new UsuarioNoIdentificadoExcepcion();
        }
    }

    @Override
    public void bloquearUsuario(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Usuario usuario = session.get(Usuario.class, id);
        if (usuario != null) {
            usuario.setActivo(false);
            session.update(usuario);
        }
    }

    @Override
    public void desbloquearUsuario(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Usuario usuario = session.get(Usuario.class, id);
        if (usuario != null) {
            usuario.setActivo(true);
            session.update(usuario);
        }
    }

    @Override
    public List<Usuario> listarUsuariosDesbloqueados() {
        Session session = sessionFactory.getCurrentSession();
        Query<Usuario> query = session.createQuery("FROM Usuario WHERE activo = true", Usuario.class);
        return query.getResultList();
    }

    @Override
    public List<Usuario> listarUsuariosBloqueados() {
        Session session = sessionFactory.getCurrentSession();
        Query<Usuario> query = session.createQuery("FROM Usuario WHERE activo = false", Usuario.class);
        return query.getResultList();
    }

    private Boolean validarPassword(String password){
        boolean esMayuscula = false, esNumero = false, esCaracterEspecial = false;
        Pattern listaEspeciales = Pattern.compile ("[?!¡@¿.,´)$(]");
        Matcher tieneEspeciales = listaEspeciales.matcher(password);
        char[] passwordArray = password.toCharArray();

        if(!password.isBlank() && password.length() >= 6){
            for(char i : passwordArray){
                if(Character.isUpperCase(i)){
                    esMayuscula = true;
                }else if(Character.isDigit(i)){
                    esNumero = true;
                }else if(tieneEspeciales.find()){
                    esCaracterEspecial = true;
                }
            }
        }else{
            return false;
        }

        return esMayuscula && esNumero && esCaracterEspecial;
    }

}
