package com.lazycece.au;

import com.lazycece.au.filter.AuFilter;

/**
 * @author lazycece
 * @date 2019/10/03
 */
public interface FilterFactory {

    AuFilter newInstance(Class clazz) throws Exception;
}
