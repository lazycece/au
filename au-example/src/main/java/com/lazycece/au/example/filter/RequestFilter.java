package com.lazycece.au.example.filter;

import com.lazycece.au.context.RequestContext;
import com.lazycece.au.filter.AuFilter;
import com.lazycece.au.http.HttpServletRequestWrapper;
import com.lazycece.au.http.HttpServletResponseWrapper;
import com.lazycece.au.log.AuLogger;
import com.lazycece.au.log.AuLoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author lazycece
 * @date 2019/11/15
 */
public class RequestFilter implements AuFilter {

    private final AuLogger log = AuLoggerFactory.getLogger(this.getClass());

    @Override
    public String name() {
        return "request-filter";
    }

    @Override
    public boolean preHandle() throws Exception {
        log.info("request filter pre-handle");
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        if (request instanceof HttpServletRequestWrapper) {
            HttpServletRequestWrapper requestWrapper = (HttpServletRequestWrapper) request;
            Map<String, String[]> parameters = requestWrapper.getParameters();
            if (parameters != null && parameters.size() > 0) {
                parameters.forEach((key, value) -> log.info("parameter -> {} = {}", key, value[0]));
            }
            byte[] body = requestWrapper.getContent();
            if (body != null && body.length > 0) {
                String bodyStr = new String(body, StandardCharsets.UTF_8);
                log.info("request body -> {}", bodyStr);
            }
        } else {
            log.warn("Au is not wrapped, you can set it up by <code>AuManage.setWrapper(true)</code>");
        }
        return true;
    }

    @Override
    public void postHandle() throws Exception {
        log.info("request filter post-handle");
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletResponse response = context.getResponse();
        if (response instanceof HttpServletResponseWrapper) {
            HttpServletResponseWrapper responseWrapper = (HttpServletResponseWrapper) response;
            String wrapperBody = "wrapper body: ";
            byte[] body = responseWrapper.getContent();
            if (body != null && body.length > 0) {
                wrapperBody += new String(body, StandardCharsets.UTF_8);
            }
            context.setResponseBody(wrapperBody);
        } else {
            log.warn("Au is not wrapped, you can set it up by <code>AuManage.setWrapper(true)</code>");
        }
    }
}
