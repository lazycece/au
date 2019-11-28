package com.lazycece.au.matcher;

/**
 * @author lazycece
 * @date 2019/11/11
 */
public interface PathMatcher {

    /**
     * To match path by pattern
     *
     * @param pattern path pattern
     * @param path    path
     * @return {@code true} if matched, else {@code false}
     */
    boolean match(String pattern, String path);
}
