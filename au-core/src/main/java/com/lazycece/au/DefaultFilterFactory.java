package com.lazycece.au;

import com.lazycece.au.filter.AuFilter;

/**
 * @author lazycece
 * @date 2019/10/03
 */
public class DefaultFilterFactory implements FilterFactory {

    @Override
    public AuFilter newInstance(Class clazz) throws InstantiationException, IllegalAccessException {
        return (AuFilter) clazz.newInstance();
    }
}
