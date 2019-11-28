package com.lazycece.au.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lazycece
 * @date 2019/11/11
 */
public class Slf4jLogger implements AuLogger {

    private final Logger log;

    Slf4jLogger(Class<?> clazz) {
        this.log = LoggerFactory.getLogger(clazz);
    }

    @Override
    public String getName() {
        return this.log.getName();
    }

    @Override
    public boolean isDebugEnabled() {
        return this.log.isDebugEnabled();
    }

    @Override
    public void debug(String s) {
        this.log.debug(s);
    }

    @Override
    public void debug(String s, Object o) {
        this.log.debug(s, o);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        this.log.debug(s, o, o1);
    }

    @Override
    public void debug(String s, Object... objects) {
        this.log.debug(s, objects);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        this.log.debug(s, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.log.isInfoEnabled();
    }

    @Override
    public void info(String s) {
        this.log.info(s);
    }

    @Override
    public void info(String s, Object o) {
        this.log.info(s, o);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        this.log.info(s, o, o1);
    }

    @Override
    public void info(String s, Object... objects) {
        this.log.info(s, objects);
    }

    @Override
    public void info(String s, Throwable throwable) {
        this.log.info(s, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.log.isWarnEnabled();
    }

    @Override
    public void warn(String s) {
        this.log.warn(s);
    }

    @Override
    public void warn(String s, Object o) {
        this.log.warn(s, o);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        this.log.warn(s, o, o1);
    }

    @Override
    public void warn(String s, Object... objects) {
        this.log.warn(s, objects);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        this.log.warn(s, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.log.isErrorEnabled();
    }

    @Override
    public void error(String s) {
        this.log.error(s);
    }

    @Override
    public void error(String s, Object o) {
        this.log.error(s, o);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        this.log.error(s, o1);
    }

    @Override
    public void error(String s, Object... objects) {
        this.log.error(s, objects);
    }

    @Override
    public void error(String s, Throwable throwable) {
        this.log.error(s, throwable);
    }
}
