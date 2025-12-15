package br.com.ifsudestemg.bean;

import java.io.Serializable;

import br.com.ifsudestemg.dao.DAO;
import br.com.ifsudestemg.model.Agendamento;
import br.com.ifsudestemg.model.PessoaAgendada;
import br.com.ifsudestemg.util.JPAUtil;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

@Named
@ViewScoped
public class CancelamentoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codigoBusca;
    private String emailBusca;
    private Agendamento agendamentoEncontrado;

    public void buscar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT a FROM Agendamento a WHERE a.codigoConfirmacao = :cod AND a.emailResponsavel = :email";
            TypedQuery<Agendamento> query = em.createQuery(jpql, Agendamento.class);
            query.setParameter("cod", this.codigoBusca.trim().toUpperCase());
            query.setParameter("email", this.emailBusca.trim());
            
            this.agendamentoEncontrado = query.getSingleResult();
            
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Encontrado", "Agendamento localizado!"));
            
        } catch (NoResultException e) {
            this.agendamentoEncontrado = null;
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não encontrado", "Verifique o Código e o E-mail informados."));
        } finally {
            em.close();
        }
    }

    public void cancelarPessoa(PessoaAgendada p) {
        try {
            this.agendamentoEncontrado.getPessoas().remove(p);
            
            if (this.agendamentoEncontrado.getPessoas().isEmpty()) {
                cancelarAgendamento();
                return;
            }
            new DAO<Agendamento>(Agendamento.class).atualiza(this.agendamentoEncontrado);
            
            new DAO<PessoaAgendada>(PessoaAgendada.class).remove(p);


            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Cancelado", "Visitante removido do grupo."));
            
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao cancelar visitante."));
            e.printStackTrace();
        }
    }

    public void cancelarAgendamento() {
        try {
            new DAO<Agendamento>(Agendamento.class).remove(this.agendamentoEncontrado);
            
            this.agendamentoEncontrado = null;
            this.codigoBusca = "";
            this.emailBusca = "";
            
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Cancelado", "Agendamento cancelado integralmente."));
            
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao cancelar agendamento."));
            e.printStackTrace();
        }
    }

    public String getCodigoBusca() { return codigoBusca; }
    public void setCodigoBusca(String codigoBusca) { this.codigoBusca = codigoBusca; }
    public String getEmailBusca() { return emailBusca; }
    public void setEmailBusca(String emailBusca) { this.emailBusca = emailBusca; }
    public Agendamento getAgendamentoEncontrado() { return agendamentoEncontrado; }
    public void setAgendamentoEncontrado(Agendamento agendamentoEncontrado) { this.agendamentoEncontrado = agendamentoEncontrado; }
}