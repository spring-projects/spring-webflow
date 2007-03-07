package org.springframework.util;

import org.springframework.beans.factory.support.MethodReplacer;

import java.lang.reflect.Method;

/**
 * Base abstract implementation of resource holder that implement method replacment logic.
 *
 * @author Maxim Petrashev
 */
public abstract class AbstractResourceHolder<E> implements ResourceHolder<E>
        , MethodReplacer {//todo replace on injector in config
    public Object reimplement(Object aObj, Method aMethod, Object[] aArgs) throws Throwable {
        Object retVal = null;
        if( aArgs.length == 0 ){
            retVal = get();
        } else {
            set( (E) aArgs[0] );
        }
        return retVal;
    }
}
