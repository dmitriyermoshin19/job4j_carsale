package ru.job4j.servlets;

import ru.job4j.models.User;
import ru.job4j.store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        User user = PsqlStore.instOf().findUserByLogin(login);
        int id = user.getId();
        if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
            HttpSession sc = req.getSession();
            sc.setAttribute("user", login);
            sc.setAttribute("userUUID", id);
            resp.setHeader("userUUID", String.valueOf(id));
        } else {
            resp.setStatus(401);
            req.setAttribute("error", "Не верный email или пароль");
        }
    }
}
