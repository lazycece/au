package com.lazycece.au;

import com.lazycece.au.filter.AuFilter;

import java.lang.reflect.InvocationTargetException;

/**
 * @author lazycece
 * @date 2019/10/03
 */
public class DefaultFilterFactory implements FilterFactory {

    @Override
    public AuFilter newInstance(Class<? extends AuFilter> clazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return clazz.getDeclaredConstructor().newInstance();
    }
}
