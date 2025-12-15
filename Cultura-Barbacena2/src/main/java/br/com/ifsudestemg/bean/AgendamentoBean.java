package br.com.ifsudestemg.bean;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import br.com.ifsudestemg.dao.DAO;
import br.com.ifsudestemg.model.Agendamento;
import br.com.ifsudestemg.model.Museu;
import br.com.ifsudestemg.model.PessoaAgendada;
import br.com.ifsudestemg.model.Usuario;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class AgendamentoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Agendamento agendamento = new Agendamento();
    private PessoaAgendada pessoa = new PessoaAgendada();
    
    private List<Agendamento> listaAgendamentos;
    private List<Museu> listaMuseus;
    
    private Long museuSelecionadoId;
    
    private String mensagemDisponibilidade = "";
    private boolean horarioValido = false;

    public void verificarDisponibilidade() {
        this.mensagemDisponibilidade = "";
        this.horarioValido = false;

        if (this.museuSelecionadoId == null || this.agendamento.getDataVisita() == null || this.agendamento.getHorarioVisita() == null) {
            return;
        }

        java.time.DayOfWeek diaSemana = this.agendamento.getDataVisita().getDayOfWeek();
        if (diaSemana == java.time.DayOfWeek.MONDAY) {
            this.mensagemDisponibilidade = "Fechado às segundas-feiras.";
            return;
        }

        int hora = this.agendamento.getHorarioVisita().getHour();
        int capacidadeTotal = 0;
        
        if (this.museuSelecionadoId == 1L) {
            if (hora < 8 || hora > 17) {
                 this.mensagemDisponibilidade = "Museu Municipal funciona de 08h às 18h.";
                 return;
            }
            capacidadeTotal = 5; 
            
        } else if (this.museuSelecionadoId == 2L) { 
            if (hora < 9 || hora > 16) {
                this.mensagemDisponibilidade = "Museu da Loucura funciona de 09h às 17h.";
                return;
           }
           capacidadeTotal = 10;
        }

        DAO<Agendamento> dao = new DAO<>(Agendamento.class);
        Long ocupadas = dao.contarVagasOcupadas(this.museuSelecionadoId, this.agendamento.getDataVisita(), this.agendamento.getHorarioVisita());
        
        long restantes = capacidadeTotal - ocupadas;

        if (restantes <= 0) {
            this.mensagemDisponibilidade = "Horário LOTADO (" + ocupadas + "/" + capacidadeTotal + "). Escolha outro.";
            this.horarioValido = false;
        } else {
            this.mensagemDisponibilidade = "Disponível! Vagas restantes: " + restantes;
            this.horarioValido = true;
        }
    }

    public void adicionarPessoa() {
        this.pessoa.setAgendamento(this.agendamento);
        this.agendamento.getPessoas().add(this.pessoa);
        this.pessoa = new PessoaAgendada();
    }
    
    public void removerPessoa(PessoaAgendada p) {
        this.agendamento.getPessoas().remove(p);
    }
    
    public void remover(Agendamento agendamento) {
        try {
            new DAO<Agendamento>(Agendamento.class).remove(agendamento);
            this.listaAgendamentos = null; 
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Agendamento removido!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível remover: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public void gravar() {
        verificarDisponibilidade(); 
        
        if (!horarioValido) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Horário inválido ou lotado. Verifique os dados."));
            return;
        }

        if (this.agendamento.getDataVisita().isBefore(java.time.LocalDate.now())) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro de Data", "Você não pode agendar para o passado!"));
            return;
        }

        if (this.agendamento.getPessoas().isEmpty()) {
             FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Adicione pelo menos um visitante na lista."));
                return;
        }
        
        DAO<Agendamento> dao = new DAO<>(Agendamento.class);
        Long ocupadas = dao.contarVagasOcupadas(this.museuSelecionadoId, this.agendamento.getDataVisita(), this.agendamento.getHorarioVisita());
        int capacidadeTotal = (this.museuSelecionadoId == 1L) ? 5 : 10;
        
        if ((ocupadas + this.agendamento.getPessoas().size()) > capacidadeTotal) {
             FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Capacidade Excedida", "O grupo é maior que as vagas restantes (" + (capacidadeTotal - ocupadas) + ")."));
             return;
        }

        Museu museu = new DAO<Museu>(Museu.class).buscaPorld(this.museuSelecionadoId);
        this.agendamento.setMuseu(museu);
        
        if (this.agendamento.getCodigoConfirmacao() == null) {
            this.agendamento.setCodigoConfirmacao(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        try {
            dao.adiciona(this.agendamento);

            this.agendamento = new Agendamento();
            this.pessoa = new PessoaAgendada();
            this.museuSelecionadoId = null;
            this.listaAgendamentos = null;
            this.mensagemDisponibilidade = "";

            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Agendamento realizado!"));
            
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro Interno", "Falha ao gravar no banco: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public void atualizarPresenca(PessoaAgendada p) {
        try {
            new DAO<PessoaAgendada>(PessoaAgendada.class).atualiza(p);
            
            String status = p.isCompareceu() ? "Presente" : "Ausente";
            
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Check-in", "Visitante " + p.getNome() + " marcado como: " + status));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao atualizar presença."));
            e.printStackTrace();
        }
    }
    
    public List<Agendamento> getAgendamentos() {
        if (this.listaAgendamentos == null) {
            Usuario usuarioLogado = (Usuario) FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get("usuarioLogado");

            DAO<Agendamento> dao = new DAO<>(Agendamento.class);

            if (usuarioLogado != null && "FUNCIONARIO".equals(usuarioLogado.getPerfil()) && usuarioLogado.getMuseu() != null) {
                this.listaAgendamentos = dao.listaPorMuseu(usuarioLogado.getMuseu().getId());
            } else {
                this.listaAgendamentos = dao.listaTodos();
            }
        }
        return this.listaAgendamentos;
    }
    
    public List<Museu> getListaMuseus() {
        if (this.listaMuseus == null) {
            this.listaMuseus = new DAO<Museu>(Museu.class).listaTodos();
        }
        return this.listaMuseus;
    }
    public Agendamento getAgendamento() { return agendamento; }
    public void setAgendamento(Agendamento agendamento) { this.agendamento = agendamento; }
    public PessoaAgendada getPessoa() { return pessoa; }
    public void setPessoa(PessoaAgendada pessoa) { this.pessoa = pessoa; }
    public Long getMuseuSelecionadoId() { return museuSelecionadoId; }
    public void setMuseuSelecionadoId(Long museuSelecionadoId) { this.museuSelecionadoId = museuSelecionadoId; }
    public String getMensagemDisponibilidade() { return mensagemDisponibilidade; }
    public void setMensagemDisponibilidade(String mensagemDisponibilidade) { this.mensagemDisponibilidade = mensagemDisponibilidade; }
}