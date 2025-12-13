package br.com.ifsudestemg.bean;

import java.io.Serializable;

import br.com.ifsudestemg.dao.UsuarioDAO;
import br.com.ifsudestemg.model.Usuario;

// IMPORTANTE: Estas são as anotações corretas para Tomcat 11 + Jakarta EE 10
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
        boolean existe = dao.existe(this.usuario);

        if (existe) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuarioLogado", this.usuario);
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