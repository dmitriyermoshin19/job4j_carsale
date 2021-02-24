package ru.job4j.servlets;

import ru.job4j.models.User;
import ru.job4j.store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class RegServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        User user = new User(login);
        user.setPassword(password);
        int id = PsqlStore.instOf().createUser(user);
        HttpSession sc = req.getSession();
        sc.setAttribute("user", login);
        sc.setAttribute("userUUID", id);
        req.getRequestDispatcher("index.html").forward(req, resp);
    }
}
