package com.lazycece.au.filter;

import com.lazycece.au.matcher.AntPathMatcher;
import com.lazycece.au.matcher.PathMatcher;

import java.util.List;

/**
 * @author lazycece
 * @date 2019/11/11
 */
public class RunnableFilter implements AuFilter {

    private AuFilter auFilter;
    private List<String> includePatterns;
    private List<String> excludePatterns;
    private PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public String name() {
        String name = this.auFilter.name();
        if (name == null) {
            name = this.auFilter.getClass().getName();
        }
        return name;
    }

    public RunnableFilter(AuFilter auFilter, List<String> includePatterns, List<String> excludePatterns) {
        this.auFilter = auFilter;
        this.includePatterns = includePatterns;
        this.excludePatterns = excludePatterns;
    }

    @Override
    public boolean preHandle() throws Exception {
        return this.auFilter.preHandle();
    }

    @Override
    public void postHandle() throws Exception {
        this.auFilter.postHandle();
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public boolean matches(String path, PathMatcher pathMatcher) {
        if (pathMatcher == null) {
            pathMatcher = this.pathMatcher;
        }
        if (this.excludePatterns != null && !this.excludePatterns.isEmpty()) {
            for (String pattern : this.excludePatterns) {
                if (pathMatcher.match(pattern, path)) {
                    return false;
                }
            }
        }

        if (this.includePatterns != null && !this.includePatterns.isEmpty()) {
            for (String pattern : this.includePatterns) {
                if (pathMatcher.match(pattern, path)) {
                    return true;
                }
            }
        }
        return false;
    }
}
