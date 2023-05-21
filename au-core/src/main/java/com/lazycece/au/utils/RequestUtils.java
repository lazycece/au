package com.lazycece.au.utils;

import com.lazycece.au.context.RequestContext;
import com.lazycece.au.log.AuLogger;
import com.lazycece.au.log.AuLoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RequestUtils {

    private final static RequestUtils INSTANCE = new RequestUtils();
    private static final String X_FORWARDED_FOR_HEADER = "x-forwarded-for";
    private final AuLogger log = AuLoggerFactory.getLogger(this.getClass());

    public static RequestUtils getInstance() {
        return INSTANCE;
    }

    public String getHeader(String name) {
        return RequestContext.getCurrentContext().getRequest().getHeader(name);
    }

    public String getParameter(String name) {
        return RequestContext.getCurrentContext().getRequest().getParameter(name);
    }

    public String getEncoding() {
        String enc = RequestContext.getCurrentContext().getRequest().getCharacterEncoding();
        if (enc == null) {
            enc = StandardCharsets.UTF_8.name();
        }
        return enc;
    }

    public Map<String, List<String>> getHeaderMap() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        Map<String, List<String>> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                if (name != null && !name.isEmpty()) {
                    Enumeration<String> headerValues = request.getHeaders(name);
                    List<String> valueList = new ArrayList<>();
                    while (headerValues.hasMoreElements()) {
                        valueList.add(headerValues.nextElement());
                    }
                    headers.put(name, valueList);
                }
            }
        }
        return Collections.unmodifiableMap(headers);

    }

    public Map<String, List<String>> getQueryParams() {
        Map<String, List<String>> qp = RequestContext.getCurrentContext().getRequestQueryParams();
        if (qp != null) {
            return qp;
        }
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        if (request.getQueryString() == null) {
            return null;
        }
        qp = parseQueryString(request.getQueryString());
        RequestContext.getCurrentContext().setRequestQueryParams(qp);
        return qp;
    }

    public Map<String, List<String>> parseQueryString(String queryString) {
        Map<String, List<String>> paramsMap = new HashMap<>(16);
        StringTokenizer st = new StringTokenizer(queryString, "&");
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            int i = s.indexOf("=");
            String name = s.substring(0, i);
            try {
                name = URLDecoder.decode(name, StandardCharsets.UTF_8.name());
            } catch (Exception e) {
                log.error("url decode error: {}", e.getMessage());
            }
            String value = "";
            if (i > 0 && s.length() >= i + 1) {
                value = s.substring(i + 1);
                try {
                    value = URLDecoder.decode(value, StandardCharsets.UTF_8.name());
                } catch (Exception e) {
                    log.error("url decode error: {}", e.getMessage());
                }
            }
            List<String> valueList = paramsMap.computeIfAbsent(name, k -> new ArrayList<>());
            valueList.add(value);
        }
        return paramsMap;
    }

    public String getRemoteAddr() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String xForwardedFor = request.getHeader(X_FORWARDED_FOR_HEADER);
        if (xForwardedFor == null) {
            return request.getRemoteAddr();
        }
        xForwardedFor = xForwardedFor.trim();
        String[] tokenize = xForwardedFor.split(",");
        if (tokenize.length == 0) {
            return null;
        } else {
            return tokenize[0].trim();
        }
    }
}

