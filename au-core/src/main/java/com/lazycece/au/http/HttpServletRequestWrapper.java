package com.lazycece.au.http;

import com.lazycece.au.log.AuLogger;
import com.lazycece.au.log.AuLoggerFactory;
import com.lazycece.au.utils.RequestUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * wrapper http servlet request for multiple reading
 *
 * @author lazycece
 * @date 2019/11/11
 */
@SuppressWarnings("unchecked")
public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {

    private final AuLogger log = AuLoggerFactory.getLogger(this.getClass());
    private HttpServletRequest request;
    /**
     * body content
     */
    private byte[] content = null;
    /**
     * request parameters
     */
    private Map<String, String[]> parameters = null;
    /**
     * Request parsed flag.
     */
    private boolean parsed = false;

    public HttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public HttpServletRequest getRequest() {
        parseRequest();
        return this.request;
    }

    public byte[] getContent() {
        parseRequest();
        return this.content;
    }

    public Map<String, String[]> getParameters() {
        parseRequest();
        if (this.parameters == null) {
            return new HashMap<>();
        }
        Map<String, String[]> map = new HashMap<>(this.parameters.size() * 2);
        this.parameters.forEach((key, values) -> map.put(key, values.clone()));
        return map;
    }

    @Override
    public ServletInputStream getInputStream() {
        parseRequest();
        return new ServletInputStreamWrapper(this.content);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream(), RequestUtils.getInstance().getEncoding()));
    }

    @Override
    public String getParameter(String name) {
        parseRequest();
        if (this.parameters == null) {
            return null;
        }
        String[] values = this.parameters.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return getParameters();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        parseRequest();
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        parseRequest();
        if (this.parameters == null) {
            return null;
        }
        String[] values = this.parameters.get(name);
        return values == null ? null : values.clone();
    }

    private void parseRequest() {
        try {
            if (this.parsed) {
                return;
            }

            Map<String, String[]> map = new HashMap<>(16);
            Map<String, List<String>> paramsMap = new HashMap<>(16);
            Map<String, List<String>> queryParams = RequestUtils.getInstance().getQueryParams();
            if (queryParams != null) {
                paramsMap.putAll(queryParams);
            }

            if (shouldBufferBody()) {
                log.debug("parse body content");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    long readBeginTime = System.currentTimeMillis();
                    IOUtils.copy(this.request.getInputStream(), baos);
                    log.debug("read buffer cost {} ms", System.currentTimeMillis() - readBeginTime);
                    this.content = baos.toByteArray();
                } catch (SocketTimeoutException e) {
                    // This can happen if the request body is smaller than the size specified in the
                    // Content-Length header, and using tomcat APR connector.
                    log.error("read buffer error: {}", e.getMessage());
                    if (this.content == null) {
                        this.content = new byte[0];
                    }
                }

                if (this.request.getContentLength() != this.content.length) {
                    log.warn("content-length({}) different from current content-length({})",
                            this.request.getContentLength(), this.content.length);
                }
                String contentType = this.request.getContentType();
                log.debug("request content type -> {}", contentType);
                final boolean post = "POST".equals(this.request.getMethod());
                final boolean formUrlencoded = contentType != null && contentType.contains("application/x-www-form-urlencoded");
                // special process for application/x-www-form-urlencoded
                if (post && formUrlencoded) {
                    String fromQueryString = new String(this.content, RequestUtils.getInstance().getEncoding());
                    Map<String, List<String>> formMap = RequestUtils.getInstance().parseQueryString(fromQueryString);
                    formMap.forEach((name, values) -> {
                        List<String> list = paramsMap.computeIfAbsent(name, k -> new LinkedList<>());
                        list.addAll(values);
                    });
                }
            }

            paramsMap.forEach((key, values) -> map.put(key, values.toArray(new String[0])));
            this.parameters = map;
            this.parsed = true;
        } catch (IOException e) {
            throw new IllegalStateException("parse request error: {}", e);
        }
    }

    private boolean shouldBufferBody() {
        if (this.request.getContentLength() > 0) {
            return true;
        } else if (this.request.getContentLength() == -1) {
            final String transferEncoding = this.request.getHeader("transfer-encoding");
            if (transferEncoding != null && transferEncoding.equals("chunked")) {
                return true;
            }
        }
        return false;
    }
}
