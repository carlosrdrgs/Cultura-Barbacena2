package br.com.ifsudestemg.dao;

import br.com.ifsudestemg.model.Usuario;
import br.com.ifsudestemg.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;

public class UsuarioDAO implements Serializable {

    private static final long serialVersionUID = 1L;

    public boolean existe(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "select u from Usuario u where u.login = :pLogin and u.senha = :pSenha";
            
            TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
            query.setParameter("pLogin", usuario.getLogin());
            query.setParameter("pSenha", usuario.getSenha());

            query.getSingleResult();
            return true;

        } catch (NoResultException e) {
            return false;
        } finally {
            em.close();
        }
    }
}