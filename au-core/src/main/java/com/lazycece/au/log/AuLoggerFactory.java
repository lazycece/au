package com.lazycece.au.log;

/**
 * @author lazycece
 * @date 2019/11/11
 */
public class AuLoggerFactory {

    public static AuLogger getLogger(Class<?> clazz) {
        return new Slf4jLogger(clazz);
    }
}
