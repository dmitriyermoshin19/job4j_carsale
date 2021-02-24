package ru.job4j.servlets;

import com.google.gson.Gson;
import ru.job4j.models.Model;
import ru.job4j.store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ModelServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer brandId = Integer.valueOf(req.getParameter("brandId"));
        List<Model> modelList = PsqlStore.instOf().findModelByBrandId(brandId);
        String modelJson = new Gson().toJson(modelList);
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(modelJson);
        out.flush();
    }
}
