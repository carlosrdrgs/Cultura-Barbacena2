package br.com.ifsudestemg.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import br.com.ifsudestemg.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;

public class DAO<T> {

    private final Class<T> classe;

    public DAO(Class<T> classe) {
        this.classe = classe;
    }

    public void adiciona(T t) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(t);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void remove(T t) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.merge(t));
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void atualiza(T t) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(t);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<T> listaTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            CriteriaQuery<T> query = em.getCriteriaBuilder().createQuery(classe);
            query.select(query.from(classe));
            return em.createQuery(query).getResultList();
        } finally {
            em.close();
        }
    }

    public T buscaPorld(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(classe, id);
        } finally {
            em.close();
        }
    }

    public Long contarVagasOcupadas(Long museuId, LocalDate data, LocalTime horario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(p) FROM PessoaAgendada p "
                        + "WHERE p.agendamento.museu.id = :museuId "
                        + "AND p.agendamento.dataVisita = :data "
                        + "AND p.agendamento.horarioVisita = :horario";
            
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("museuId", museuId);
            query.setParameter("data", data);
            query.setParameter("horario", horario);
            
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
    
    public List<T> listaPorMuseu(Long museuId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT a FROM " + classe.getSimpleName() + " a WHERE a.museu.id = :museuId";
            TypedQuery<T> query = em.createQuery(jpql, classe);
            query.setParameter("museuId", museuId);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }
}