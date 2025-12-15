package br.com.ifsudestemg.bean;

import br.com.ifsudestemg.dao.DAO;
import br.com.ifsudestemg.model.Agendamento;
import br.com.ifsudestemg.model.PessoaAgendada;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@RequestScoped
public class DashboardBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int totalAgendamentos;
    private int totalVisitantes;

    @PostConstruct
    public void init() {
        List<Agendamento> listaAgendamentos = new DAO<Agendamento>(Agendamento.class).listaTodos();
        List<PessoaAgendada> listaPessoas = new DAO<PessoaAgendada>(PessoaAgendada.class).listaTodos();
        
        this.totalAgendamentos = listaAgendamentos.size();
        this.totalVisitantes = listaPessoas.size();
    }

    // Getters
    public int getTotalAgendamentos() { return totalAgendamentos; }
    public int getTotalVisitantes() { return totalVisitantes; }
}