package br.com.ifsudestemg.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Agendamento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataVisita;
    private LocalTime horarioVisita;
    private String emailResponsavel;
    private String codigoConfirmacao;
    private String observacoes;

    @ManyToOne
    private Museu museu;

    @OneToMany(mappedBy = "agendamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PessoaAgendada> pessoas = new ArrayList<>();

    public Agendamento() {}
    
    public void adicionarPessoa(PessoaAgendada pessoa) {
        pessoa.setAgendamento(this);
        this.pessoas.add(pessoa);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDataVisita() { return dataVisita; }
    public void setDataVisita(LocalDate dataVisita) { this.dataVisita = dataVisita; }

    public LocalTime getHorarioVisita() { return horarioVisita; }
    public void setHorarioVisita(LocalTime horarioVisita) { this.horarioVisita = horarioVisita; }

    public String getEmailResponsavel() { return emailResponsavel; }
    public void setEmailResponsavel(String emailResponsavel) { this.emailResponsavel = emailResponsavel; }

    public String getCodigoConfirmacao() { return codigoConfirmacao; }
    public void setCodigoConfirmacao(String codigoConfirmacao) { this.codigoConfirmacao = codigoConfirmacao; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Museu getMuseu() { return museu; }
    public void setMuseu(Museu museu) { this.museu = museu; }

    public List<PessoaAgendada> getPessoas() { return pessoas; }
    public void setPessoas(List<PessoaAgendada> pessoas) { this.pessoas = pessoas; }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Agendamento other = (Agendamento) obj;
        return Objects.equals(id, other.id);
    }
}