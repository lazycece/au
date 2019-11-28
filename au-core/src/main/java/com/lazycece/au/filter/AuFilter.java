package com.lazycece.au.filter;

/**
 * @author lazycece
 * @date 2019/10/03
 */
public interface AuFilter {

    /**
     * Get AU-Filter's name
     * <p>
     * if <code>name()=null</code>, it will be class'name.
     *
     * @return filter's name
     */
    default String name() {
        return null;
    }

    /**
     * Pre-processing on filter in AU filter chain.
     *
     * @return true, indicates the filter should not be continue,
     * otherwise false.
     * @throws Exception exception
     */
    default boolean preHandle() throws Exception {
        return true;
    }

    /**
     * Post-processing on filter in AU filter chain.
     *
     * @throws Exception exception
     */
    default void postHandle() throws Exception {
    }
}
