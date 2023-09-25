package co.xiaowangzi.debug.runtime;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(filterName = "characterFilter" ,urlPatterns = "/*")
public class CharacterFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        String codingMode="utf-8";
        req.setCharacterEncoding(codingMode);
        resp.setCharacterEncoding(codingMode);
        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {

    }
}
