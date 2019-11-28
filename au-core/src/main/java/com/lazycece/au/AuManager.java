package com.lazycece.au;

import com.lazycece.au.filter.AuFilter;
import com.lazycece.au.filter.FilterRegistration;
import com.lazycece.au.filter.FilterRegistry;
import com.lazycece.au.exception.AuException;
import com.lazycece.au.filter.RunnableFilter;
import com.lazycece.au.matcher.PathMatcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author lazycece
 * @date 2019/11/11
 */
public class AuManager {

    private static final AuManager INSTANCE = new AuManager();
    private final FilterRegistry filterRegistry = new FilterRegistry();
    private final FilterFactory filterFactory = new DefaultFilterFactory();
    /**
     * Global matcher for path. if it not be set specially here or in ${@link FilterRegistration#pathMatcher},
     * default is ${@link com.lazycece.au.matcher.AntPathMatcher} at runtime(${@link RunnableFilter#pathMatcher).
     */
    private PathMatcher pathMatcher;
    private boolean wrapper = true;

    public static AuManager getInstance() {
        return INSTANCE;
    }

    public FilterRegistration addAuFilter(AuFilter auFilter) {
        return this.filterRegistry.addAuFilter(auFilter);
    }

    public FilterRegistration addAuFilter(Class<? extends AuFilter> clazz) {
        AuFilter auFilter;
        try {
            auFilter = filterFactory.newInstance(clazz);
        } catch (Exception e) {
            throw new AuException(e);
        }
        return this.addAuFilter(auFilter);
    }

    public RunnableFilter getRunnableFilter(String name) {
        FilterRegistration filterRegistration = this.filterRegistry.getRegistration(name);
        return filterRegistration == null ? null : filterRegistration.getRunnableFilter();
    }

    public List<RunnableFilter> getRunnableFilters() {
        List<FilterRegistration> registrationList = new ArrayList<>(this.filterRegistry.getRegistrations());
        registrationList.sort(Comparator.comparingInt(FilterRegistration::getOrder));
        List<RunnableFilter> filterList = new ArrayList<>();
        registrationList.forEach(filterRegistration -> filterList.add(filterRegistration.getRunnableFilter()));
        return filterList;
    }

    public FilterRegistry getFilterRegistry() {
        return this.filterRegistry;
    }

    public FilterFactory getFilterFactory() {
        return this.filterFactory;
    }

    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public boolean isWrapper() {
        return wrapper;
    }

    public void setWrapper(boolean wrapper) {
        this.wrapper = wrapper;
    }
}
