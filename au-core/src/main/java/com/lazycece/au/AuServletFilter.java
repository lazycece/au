package com.lazycece.au;

import com.lazycece.au.context.RequestContext;
import com.lazycece.au.log.AuLogger;
import com.lazycece.au.log.AuLoggerFactory;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lazycece
 * @date 2019/11/11
 */
public class AuServletFilter implements Filter {

    private final AuLogger log = AuLoggerFactory.getLogger(this.getClass());
    private AuRunner auRunner;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("load Au");
        this.auRunner = new AuRunner();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            this.auRunner.init((HttpServletRequest) request, (HttpServletResponse) response);
            boolean permit = this.auRunner.preHandle();
            if (permit) {
                chain.doFilter(RequestContext.getCurrentContext().getRequest(), RequestContext.getCurrentContext().getResponse());
            }
            this.auRunner.postHandle();
        } finally {
            this.auRunner.unset();
        }
    }

    @Override
    public void destroy() {
    }
}
