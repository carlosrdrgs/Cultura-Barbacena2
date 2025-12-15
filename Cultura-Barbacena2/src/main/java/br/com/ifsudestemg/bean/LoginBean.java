package br.com.ifsudestemg.bean;

import java.io.Serializable;

import br.com.ifsudestemg.dao.UsuarioDAO;
import br.com.ifsudestemg.model.Usuario;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Usuario usuario = new Usuario();

    public String efetuaLogin() {
        UsuarioDAO dao = new UsuarioDAO();
        
        Usuario usuarioEncontrado = dao.existe(this.usuario);

        if (usuarioEncontrado != null) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuarioLogado", usuarioEncontrado);
            return "index?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário ou senha inválidos", "Erro"));
            return "login?faces-redirect=true";
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "login?faces-redirect=true";
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}