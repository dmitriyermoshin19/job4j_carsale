package ru.job4j.servlets;

import com.google.gson.Gson;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import ru.job4j.models.*;
import ru.job4j.store.PsqlStore;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdvertisementServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tmpdir");
        factory.setRepository(repository);
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            List<FileItem> items = upload.parseRequest(req);
            Advertisement advertisement = parseItems(items);
            if (advertisement.getPhoto() == null) {
                advertisement.setPhoto(advertisement.getTemp());
            } else {
                File folder = new File("images");
                File file = new File(folder + File.separator + advertisement.getTemp());
                file.delete();
            }
            HttpSession sc = req.getSession();
            Integer userId = (Integer) sc.getAttribute("userUUID");
            advertisement.setUser(new User(userId));
            PsqlStore.instOf().updateAdvertisement(advertisement);
            resp.setStatus(200);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String jsonString;
        if (id == null) {
            List<Advertisement> advertisementList = PsqlStore.instOf().findAllAdvertisements();
            jsonString = new Gson().toJson(advertisementList);
        } else {
            Advertisement advertisement =  PsqlStore.instOf().findAdvertisementById(Integer.valueOf(id));
            jsonString = new Gson().toJson(advertisement);
        }
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(jsonString);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tmpdir");
        factory.setRepository(repository);
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            List<FileItem> items = upload.parseRequest(req);
            Advertisement advertisement = parseItems(items);
            HttpSession sc = req.getSession();
            Integer userId = (Integer) sc.getAttribute("userUUID");
            advertisement.setUser(new User(userId));
            advertisement.setStatus(new Status(2));
            PsqlStore.instOf().createAdvertisement(advertisement);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
    }

    private Advertisement parseItems(List<FileItem> items) {
        Advertisement advertisement = new Advertisement();
        try {
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    File folder = new File("images");
                    if (!folder.exists()) {
                        folder.mkdir();
                    }
                    String fileName = System.currentTimeMillis() + "@" + item.getName();
                    File file = new File(folder + File.separator + fileName);
                    try (
                            BufferedInputStream reader = new BufferedInputStream(item.getInputStream());
                            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                    ) {
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = reader.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                    }
                    advertisement.setPhoto(fileName);
                }
                switch (item.getFieldName()) {
                    case "id":
                        advertisement.setId(Integer.valueOf(item.getString()));
                        break;
                    case "status":
                        advertisement.setStatus(new Status(Integer.valueOf(item.getString())));
                        break;
                    case "model":
                        advertisement.setModel(new Model(Integer.valueOf(item.getString())));
                        break;
                    case "numberOfOwners":
                        String theString = IOUtils.toString(item.getInputStream(), StandardCharsets.UTF_8);
                        advertisement.setOwners(theString);
                        break;
                    case "transmission":
                        advertisement.setTransmission(new Transmission(Integer.valueOf(item.getString())));
                        break;
                    case "yearIssue":
                        advertisement.setYearIssue(Integer.valueOf(item.getString()));
                        break;
                    case "description":
                        String description = IOUtils.toString(item.getInputStream(), StandardCharsets.UTF_8);
                        advertisement.setDescription(description);
                        break;
                    case "currImg":
                        advertisement.setTemp(item.getString());
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return advertisement;
    }
}
