package com.lazycece.au.example;

import com.lazycece.au.AuManager;
import com.lazycece.au.AuServletFilter;
import com.lazycece.au.example.filter.RequestFilter;
import com.lazycece.au.example.filter.SimpleAuFilter;
import com.lazycece.au.example.servlet.ExampleServlet;
import com.lazycece.au.log.AuLogger;
import com.lazycece.au.log.AuLoggerFactory;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import jakarta.servlet.DispatcherType;

import java.util.EnumSet;

/**
 * @author lazycece
 * @date 2019/11/15
 */
public class JettyApp {

    private static final AuLogger log = AuLoggerFactory.getLogger(JettyApp.class);

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        PropertyConfigurator.configure(ClassLoader.getSystemResource("log4j.properties"));
        Server server = createServer(port);
        server.start();
        log.info("server start on {}", port);
        server.join();
    }

    private static Server createServer(int port) {
        log.info("init server...");
        Server server = new Server(port);

        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setContextPath("/au");
        server.setHandler(contextHandler);

        contextHandler.addServlet(ExampleServlet.class, "/*");

        // For Au
        AuManager auManager = AuManager.getInstance();
        auManager.addAuFilter(SimpleAuFilter.class)
                .excludePatterns("/a/b")
                .includePatterns("/**")
                .order(1);
        auManager.addAuFilter(RequestFilter.class)
                .excludePatterns("/a/b")
                .includePatterns("/**");
        auManager.setWrapper(true);
        contextHandler.addFilter(AuServletFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

        return server;
    }

}
