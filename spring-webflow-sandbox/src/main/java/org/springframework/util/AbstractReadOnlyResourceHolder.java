package org.springframework.util;

/**
 * Read-only resource holder.
 *
 * @author Maxim Petrashev
 */
public abstract class AbstractReadOnlyResourceHolder<E> extends AbstractResourceHolder<E> {
    public final void set(E aObject) {
        throw new UnsupportedOperationException();
    }
}
