package com.lazycece.au.filter;

import com.lazycece.au.matcher.PathMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lazycece
 * @date 2019/11/11
 */
public class FilterRegistration {

    private final AuFilter auFilter;
    /**
     * The smaller the value of order, the higher the priority
     */
    private int order = -1;
    private final List<String> includePatterns = new ArrayList<>();
    private final List<String> excludePatterns = new ArrayList<>();
    private PathMatcher pathMatcher;

    public FilterRegistration(AuFilter auFilter) {
        this.auFilter = auFilter;
    }

    public FilterRegistration order(int order) {
        this.order = order;
        return this;
    }

    public FilterRegistration includePatterns(List<String> patterns) {
        this.includePatterns.addAll(patterns);
        return this;
    }


    public FilterRegistration includePatterns(String... patterns) {
        includePatterns(Arrays.asList(patterns));
        return this;
    }

    public FilterRegistration excludePatterns(List<String> patterns) {
        this.excludePatterns.addAll(patterns);
        return this;
    }

    public FilterRegistration excludePatterns(String... patterns) {
        excludePatterns(Arrays.asList(patterns));
        return this;
    }

    public FilterRegistration pathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
        return this;
    }

    public int getOrder() {
        return order;
    }

    public RunnableFilter getRunnableFilter() {
        RunnableFilter runnableFilter = new RunnableFilter(this.auFilter, this.includePatterns, this.excludePatterns);
        if (this.pathMatcher != null) {
            runnableFilter.setPathMatcher(this.pathMatcher);
        }
        return runnableFilter;
    }
}

