package br.com.ifsudestemg.bean;

import br.com.ifsudestemg.dao.DAO;
import br.com.ifsudestemg.model.Agendamento;
import br.com.ifsudestemg.model.Museu;
import br.com.ifsudestemg.model.PessoaAgendada;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Named
@ViewScoped
public class AgendamentoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Agendamento agendamento = new Agendamento();
    private PessoaAgendada pessoa = new PessoaAgendada();
    
    private List<Agendamento> listaAgendamentos;
    private List<Museu> listaMuseus;
    
    private Long museuSelecionadoId;


    public void adicionarPessoa() {
        this.pessoa.setAgendamento(this.agendamento);
        this.agendamento.getPessoas().add(this.pessoa);
        this.pessoa = new PessoaAgendada();
    }
    
    public void removerPessoa(PessoaAgendada p) {
        this.agendamento.getPessoas().remove(p);
    }

    public void gravar() {
        System.out.println("Iniciando validação e gravação...");

        if (this.museuSelecionadoId == null) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Selecione um museu!"));
            return;
        }

        if (this.agendamento.getDataVisita().isBefore(java.time.LocalDate.now())) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro de Data", "Você não pode agendar para o passado!"));
            return;
        }

        java.time.DayOfWeek diaSemana = this.agendamento.getDataVisita().getDayOfWeek();
        if (diaSemana == java.time.DayOfWeek.MONDAY) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Museu Fechado", "Infelizmente o museu não abre às segundas-feiras."));
            return;
        }

        if (this.agendamento.getPessoas().isEmpty()) {
             FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Adicione pelo menos um visitante na lista."));
                return;
        }

        Museu museu = new DAO<Museu>(Museu.class).buscaPorld(this.museuSelecionadoId);
        this.agendamento.setMuseu(museu);
        
        if (this.agendamento.getCodigoConfirmacao() == null) {
            this.agendamento.setCodigoConfirmacao(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        try {
            new DAO<Agendamento>(Agendamento.class).adiciona(this.agendamento);

            this.agendamento = new Agendamento();
            this.pessoa = new PessoaAgendada();
            this.museuSelecionadoId = null;
            this.listaAgendamentos = null;

            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Agendamento realizado!"));
            
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro Interno", "Falha ao gravar no banco: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public List<Agendamento> getAgendamentos() {
        if (this.listaAgendamentos == null) {
            this.listaAgendamentos = new DAO<Agendamento>(Agendamento.class).listaTodos();
        }
        return this.listaAgendamentos;
    }

    public List<Museu> getListaMuseus() {
        if (this.listaMuseus == null) {
            this.listaMuseus = new DAO<Museu>(Museu.class).listaTodos();
        }
        return this.listaMuseus;
    }

    public Agendamento getAgendamento() {
    	return agendamento;
    	}
    
    public void setAgendamento(Agendamento agendamento) {
    	this.agendamento = agendamento; 
    	}

    public PessoaAgendada getPessoa() { 
    	return pessoa; 
    }
    
    public void setPessoa(PessoaAgendada pessoa) { 
    	this.pessoa = pessoa; 
    }

    public Long getMuseuSelecionadoId() { 
    	return museuSelecionadoId; 
    }
    
    public void setMuseuSelecionadoId(Long museuSelecionadoId) { 
    	this.museuSelecionadoId = museuSelecionadoId; 
    }
}