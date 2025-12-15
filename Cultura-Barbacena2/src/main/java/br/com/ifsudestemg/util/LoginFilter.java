package br.com.ifsudestemg.util;

import br.com.ifsudestemg.model.Usuario;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("*.xhtml")
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        String url = req.getRequestURI();
        
        boolean isLoginPage = url.contains("login.xhtml");
        boolean isAgendamentoPage = url.contains("agendamento.xhtml");
        boolean isCancelamentoPage = url.contains("cancelamento.xhtml");
        boolean isResource = url.contains("javax.faces.resource") || url.contains("jakarta.faces.resource");

        boolean isAdminPage = url.contains("usuario.xhtml");

        if (usuarioLogado != null || isLoginPage || isAgendamentoPage || isCancelamentoPage || isResource) {
            
            if (isAdminPage && usuarioLogado != null && !"ADMIN".equals(usuarioLogado.getPerfil())) {
                res.sendRedirect(req.getContextPath() + "/index.xhtml");
                return;
            }

            chain.doFilter(request, response);
            
        } else {
            res.sendRedirect(req.getContextPath() + "/login.xhtml");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}