package com.lazycece.au.example;

import com.lazycece.au.AuManager;
import com.lazycece.au.AuServletFilter;
import com.lazycece.au.example.filter.RequestFilter;
import com.lazycece.au.example.filter.SimpleAuFilter;
import com.lazycece.au.example.servlet.ExampleServlet;
import com.lazycece.au.log.AuLogger;
import com.lazycece.au.log.AuLoggerFactory;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.log4j.PropertyConfigurator;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import java.io.File;

/**
 * @author lazycece
 * @date 2019/11/15
 */
public class TomcatApp {

    private static final AuLogger log = AuLoggerFactory.getLogger(TomcatApp.class);

    public static void main(String[] args) throws Exception {

        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        PropertyConfigurator.configure(ClassLoader.getSystemResource("log4j.properties"));
        Tomcat server = createServer(port);
        server.start();
        log.info("server start on {}", port);
        server.getServer().await();
    }

    private static Tomcat createServer(int port) {
        log.info("init server...");
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);

        Context ctx = tomcat.addContext("/au", new File(".").getAbsolutePath());
        Tomcat.addServlet(ctx, ExampleServlet.class.getSimpleName(), new ExampleServlet())
                .addMapping("/*");

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

        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName(AuServletFilter.class.getSimpleName());
        filterDef.setFilter(new AuServletFilter());
        ctx.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(filterDef.getFilterName());
        filterMap.addURLPattern("/*");
        ctx.addFilterMap(filterMap);

        return tomcat;
    }

}
