package com.lazycece.au.example.filter;

import com.lazycece.au.filter.AuFilter;
import com.lazycece.au.log.AuLogger;
import com.lazycece.au.log.AuLoggerFactory;

/**
 * @author lazycece
 * @date 2019/11/15
 */
public class SimpleAuFilter implements AuFilter {

    private final AuLogger log = AuLoggerFactory.getLogger(this.getClass());

    @Override
    public String name() {
        return "simple-filter";
    }

    @Override
    public boolean preHandle() throws Exception {
        log.info("simple filter pre-handle");
        return true;
    }

    @Override
    public void postHandle() throws Exception {
        log.info("simple filter post-handle");
    }
}
