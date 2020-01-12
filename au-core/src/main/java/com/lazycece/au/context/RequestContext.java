package com.lazycece.au.context;

import com.lazycece.au.log.AuLogger;
import com.lazycece.au.log.AuLoggerFactory;
import com.lazycece.au.utils.DeepCopyUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.NotSerializableException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lazycece
 */
@SuppressWarnings("unchecked")
public class RequestContext extends ConcurrentHashMap<String, Object> {

    private final AuLogger log = AuLoggerFactory.getLogger(this.getClass());

    private static Class<? extends RequestContext> contextClass = RequestContext.class;

    private static final ThreadLocal<? extends RequestContext> THREAD_LOCAL =
            ThreadLocal.withInitial(() -> {
                try {
                    return contextClass.newInstance();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });

    public RequestContext() {
        super();
    }

    public static void setContextClass(Class<? extends RequestContext> clazz) {
        contextClass = clazz;
    }

    public static RequestContext getCurrentContext() {
        return THREAD_LOCAL.get();
    }

    public HttpServletRequest getRequest() {
        return (HttpServletRequest) get(ContextKey.REQUEST);
    }

    public void setRequest(HttpServletRequest request) {
        put(ContextKey.REQUEST, request);
    }

    public HttpServletResponse getResponse() {
        return (HttpServletResponse) get(ContextKey.RESPONSE);
    }

    public void setResponse(HttpServletResponse response) {
        put(ContextKey.RESPONSE, response);
    }

    public Map<String, List<String>> getRequestQueryParams() {
        return (Map<String, List<String>>) get(ContextKey.REQUEST_QUERY_PARAMS);
    }

    public void setRequestQueryParams(Map<String, List<String>> requestQueryParams) {
        put(ContextKey.REQUEST_QUERY_PARAMS, requestQueryParams);
    }

    public void unset() {
        THREAD_LOCAL.remove();
    }

    public RequestContext copy() {
        RequestContext copy = new RequestContext();
        keySet().iterator().forEachRemaining(key -> {
            Object origin = get(key);
            try {
                Object copyValue = DeepCopyUtils.copy(origin);
                if (copyValue != null) {
                    copy.put(key, copyValue);
                } else {
                    copy.put(key, origin);
                }
            } catch (NotSerializableException e) {
                log.warn("not serializable object to be copy.");
                copy.put(key, origin);
            }
        });
        return copy;
    }

    private static class ContextKey {
        static final String REQUEST = "request";
        static final String RESPONSE = "response";
        static final String REQUEST_QUERY_PARAMS = "requestQueryParams";
    }
}
