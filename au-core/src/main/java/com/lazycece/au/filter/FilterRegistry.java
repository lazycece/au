package com.lazycece.au.filter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lazycece
 * @date 2019/11/11
 */
public class FilterRegistry {

    private final ConcurrentHashMap</*Au Filter name*/String, FilterRegistration> registrations = new ConcurrentHashMap<>();

    public FilterRegistration addAuFilter(AuFilter auFilter) {
        FilterRegistration filterRegistration = new FilterRegistration(auFilter);
        String name = auFilter.name();
        if (StringUtils.isBlank(name)) {
            name = auFilter.getClass().getName();
        }
        this.registrations.put(name, filterRegistration);
        return filterRegistration;
    }

    public void remove(String name) {
        this.registrations.remove(name);
    }

    public FilterRegistration getRegistration(String name) {
        return this.registrations.get(name);
    }

    public List<FilterRegistration> getRegistrations() {
        return new ArrayList<>(this.registrations.values());
    }
}
