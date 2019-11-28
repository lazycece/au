package com.lazycece.au.matcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author lazycece
 * @date 2019/11/27
 */
@RunWith(MockitoJUnitRunner.class)
public class PathMatcherTest {

    @Test
    public void testMatch() {

        PathMatcher pathMatcher = new AntPathMatcher();

        // ‘*’ match case
        assertThat(pathMatcher.match("/*", "/au")).isTrue();
        assertThat(pathMatcher.match("/*", "/au/false")).isFalse();

        // ‘**’ match case
        assertThat(pathMatcher.match("/**", "/au")).isTrue();
        assertThat(pathMatcher.match("/**", "/au/true")).isTrue();
        assertThat(pathMatcher.match("/**", "/au/true/true")).isTrue();

        // ‘?’ match case
        assertThat(pathMatcher.match("/?", "/a")).isTrue();
        assertThat(pathMatcher.match("/?", "/au")).isFalse();

        // match case: case-sensitive fashion
        assertThat(pathMatcher.match("/au", "/Au")).isFalse();
        AntPathMatcher antPathMatcher = (AntPathMatcher) pathMatcher;
        antPathMatcher.setCaseSensitive(false);
        assertThat(pathMatcher.match("/au", "/Au")).isTrue();
    }
}
