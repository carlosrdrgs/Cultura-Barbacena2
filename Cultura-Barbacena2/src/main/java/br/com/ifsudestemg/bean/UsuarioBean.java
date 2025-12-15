package br.com.ifsudestemg.bean;

import br.com.ifsudestemg.dao.DAO;
import br.com.ifsudestemg.model.Museu;
import br.com.ifsudestemg.model.Usuario;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class UsuarioBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Usuario usuario = new Usuario();
    private List<Usuario> listaUsuarios;
    
    private List<Museu> listaMuseus;
    private Long museuSelecionadoId;

    @PostConstruct
    public void init() {
        carregarListas();
    }

    public void carregarListas() {
        this.listaUsuarios = new DAO<Usuario>(Usuario.class).listaTodos();
        this.listaMuseus = new DAO<Museu>(Museu.class).listaTodos();
    }

    public void salvar() {
        try {
            DAO<Usuario> dao = new DAO<>(Usuario.class);
            
            if (this.museuSelecionadoId != null) {
                Museu m = new DAO<Museu>(Museu.class).buscaPorld(this.museuSelecionadoId);
                this.usuario.setMuseu(m);
            } else {
                this.usuario.setMuseu(null);
            }
            
            if (usuario.getId() == null) {
                dao.adiciona(usuario);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário cadastrado!"));
            } else {
                dao.atualiza(usuario);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Dados atualizados!"));
            }

            this.usuario = new Usuario();
            this.museuSelecionadoId = null;
            carregarListas(); 
            
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao salvar usuário."));
            e.printStackTrace();
        }
    }

    public void remover(Usuario u) {
        try {
            new DAO<Usuario>(Usuario.class).remove(u);
            carregarListas();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário removido!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível remover."));
        }
    }
    
    public void editar(Usuario u) {
        this.usuario = u;
        if (u.getMuseu() != null) {
            this.museuSelecionadoId = u.getMuseu().getId();
        } else {
            this.museuSelecionadoId = null;
        }
    }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public List<Usuario> getListaUsuarios() { return listaUsuarios; }
    public List<Museu> getListaMuseus() { return listaMuseus; }
    public Long getMuseuSelecionadoId() { return museuSelecionadoId; }
    public void setMuseuSelecionadoId(Long museuSelecionadoId) { this.museuSelecionadoId = museuSelecionadoId; }
}