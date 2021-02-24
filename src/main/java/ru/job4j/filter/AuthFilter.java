package ru.job4j.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest sreq, ServletResponse sresp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) sreq;
        HttpServletResponse resp = (HttpServletResponse) sresp;
        if (req.getSession().getAttribute("user") == null) {
            RequestDispatcher view = req.getRequestDispatcher("login.html");
            view.forward(req, resp);
            return;
        }
        chain.doFilter(sreq, sresp);
    }
}
