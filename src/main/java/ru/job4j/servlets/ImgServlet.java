package ru.job4j.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class ImgServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        resp.setContentType("id=" + id);
        resp.setContentType("image/png");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + id + "\"");
        File file = new File("images" + File.separator + id);
        try (
                BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
                BufferedOutputStream out = new BufferedOutputStream(resp.getOutputStream());
        ) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = reader.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.flush();
        }
    }
}
