package com.lazycece.au;

import com.lazycece.au.context.RequestContext;
import com.lazycece.au.exception.AuException;
import com.lazycece.au.filter.RunnableFilter;
import com.lazycece.au.http.HttpServletRequestWrapper;
import com.lazycece.au.http.HttpServletResponseWrapper;
import com.lazycece.au.log.AuLogger;
import com.lazycece.au.log.AuLoggerFactory;
import com.lazycece.au.matcher.PathMatcher;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lazycece
 * @date 2019/11/11
 */
public class AuRunner {

    private final AuLogger log = AuLoggerFactory.getLogger(this.getClass());
    private static final ThreadLocal<List<RunnableFilter>> RUNNER_CONTEXT = ThreadLocal.withInitial(ArrayList::new);
    private final PathMatcher pathMatcher;
    private final boolean wrapper;

    public AuRunner() {
        AuManager auManager = AuManager.getInstance();
        this.wrapper = auManager.isWrapper();
        this.pathMatcher = auManager.getPathMatcher();
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
        String path = request.getRequestURI();
        log.debug("request uri is {}", path);
        String contextPath = request.getContextPath();
        if (StringUtils.isNotBlank(contextPath)) {
            path = path.replace(contextPath, StringUtils.EMPTY);
        }
        log.debug("Init au filter");
        List<RunnableFilter> matchesFilters = new ArrayList<>();
        for (RunnableFilter filter : AuManager.getInstance().getRunnableFilters()) {
            boolean match = filter.matches(path, this.pathMatcher);
            log.debug("{} match {}", filter.name(), match);
            if (match) {
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

    public void unset() {
        RequestContext context = RequestContext.getCurrentContext();
        if (this.wrapper) {
            HttpServletResponse currentResponse = context.getResponse();
            if (currentResponse instanceof HttpServletResponseWrapper) {
                HttpServletResponseWrapper responseWrapper = (HttpServletResponseWrapper) currentResponse;
                ByteArrayInputStream bais = new ByteArrayInputStream(responseWrapper.getContent());
                try {
                    IOUtils.copy(bais, responseWrapper.getResponse().getOutputStream());
                } catch (IOException e) {
                    throw new AuException(e);
                }
            }
        }
        RUNNER_CONTEXT.remove();
        context.unset();
    }
}