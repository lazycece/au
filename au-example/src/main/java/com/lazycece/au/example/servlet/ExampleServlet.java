package com.lazycece.au.example.servlet;

import com.lazycece.au.log.AuLogger;
import com.lazycece.au.log.AuLoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lazycece
 * @date 2019/11/15
 */
public class ExampleServlet extends HttpServlet {
    private final AuLogger log = AuLoggerFactory.getLogger(this.getClass());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().println("<h1>Integrate Au success </h1>");
        log.info("finish request !");
    }
}
