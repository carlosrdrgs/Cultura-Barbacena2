package br.com.ifsudestemg.dao;

import br.com.ifsudestemg.model.Usuario;
import br.com.ifsudestemg.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class UsuarioDAO extends DAO<Usuario> {

    public UsuarioDAO() {
        super(Usuario.class);
    }

    public Usuario existe(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.login = :login AND u.senha = :senha", 
                    Usuario.class);
            
            query.setParameter("login", usuario.getLogin());
            query.setParameter("senha", usuario.getSenha());
            
            return query.getSingleResult();
            
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}