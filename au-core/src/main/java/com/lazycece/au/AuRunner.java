package com.lazycece.au;

import com.lazycece.au.context.RequestContext;
import com.lazycece.au.exception.AuException;
import com.lazycece.au.filter.RunnableFilter;
import com.lazycece.au.http.HttpServletRequestWrapper;
import com.lazycece.au.http.HttpServletResponseWrapper;
import com.lazycece.au.log.AuLogger;
import com.lazycece.au.log.AuLoggerFactory;
import com.lazycece.au.matcher.PathMatcher;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lazycece
 * @date 2019/11/11
 */
public class AuRunner {

    private final AuLogger log = AuLoggerFactory.getLogger(this.getClass());
    private static final ThreadLocal<List<RunnableFilter>> RUNNER_CONTEXT = ThreadLocal.withInitial(ArrayList::new);
    private final List<RunnableFilter> runnableFilters;
    private final PathMatcher pathMatcher;
    private final boolean wrapper;


    public AuRunner() {
        AuManager auManager = AuManager.getInstance();
        this.wrapper = auManager.isWrapper();
        this.pathMatcher = auManager.getPathMatcher();
        this.runnableFilters = auManager.getRunnableFilters();
        this.runnableFilters.forEach(runnableFilter -> log.info("load Au filter -> {}", runnableFilter.name()));
    }

    public void init(HttpServletRequest request, HttpServletResponse response) {
        RequestContext context = RequestContext.getCurrentContext();
        log.info("Init Au, and Au will {} request and response.", wrapper ? "wrapper the" : "use original");
        if (this.wrapper) {
            context.setRequest(new HttpServletRequestWrapper(request));
            context.setResponse(new HttpServletResponseWrapper(response));
        } else {
            context.setRequest(request);
            context.setResponse(response);
        }
        context.setOriginResponse(response);

        String path = request.getRequestURI();
        log.debug("request uri is {}", path);
        String contextPath = request.getContextPath();
        if (StringUtils.isNotBlank(contextPath)) {
            path = path.replace(contextPath, StringUtils.EMPTY);
        }

        List<RunnableFilter> matchesFilters = new ArrayList<>();
        for (RunnableFilter filter : this.runnableFilters) {
            if (filter.matches(path, this.pathMatcher)) {
                matchesFilters.add(filter);
            }
        }
        RUNNER_CONTEXT.set(matchesFilters);
    }

    public boolean preHandle() throws AuException {
        // mark permit filter
        List<RunnableFilter> permitFilters = new ArrayList<>();
        for (RunnableFilter filter : RUNNER_CONTEXT.get()) {
            try {
                boolean permit = filter.preHandle();
                if (!permit) {
                    // if filter has been prevented, update context filter.
                    RUNNER_CONTEXT.set(permitFilters);
                    return false;
                } else {
                    permitFilters.add(filter);
                }
            } catch (Exception e) {
                throw new AuException(e);
            }
        }
        return true;
    }

    public void postHandle() {
        List<RunnableFilter> filters = RUNNER_CONTEXT.get();
        for (int i = filters.size() - 1; i >= 0; i--) {
            try {
                filters.get(i).postHandle();
            } catch (Exception e) {
                throw new AuException(e);
            }
        }
    }

    public void completion() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletResponse response = context.getOriginResponse();
        if (response == null) {
            throw new AuException("original response not exist.");
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String responseBody = context.getResponseBody();
        try {
            if (StringUtils.isNotBlank(responseBody)) {
                response.getWriter().println(responseBody);
                return;
            }
            if (!this.wrapper) {
                return;
            }
            HttpServletResponse currentResponse = context.getResponse();
            if (currentResponse instanceof HttpServletResponseWrapper) {
                HttpServletResponseWrapper responseWrapper = (HttpServletResponseWrapper) currentResponse;
                response.getWriter().println(new String(responseWrapper.getContent(), StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new AuException(e);
        }
    }

    public void unset() {
        RUNNER_CONTEXT.remove();
        RequestContext.getCurrentContext().unset();
    }
}