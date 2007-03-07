package org.springframework.util;

/**
 * Base interface for object holder concept.
 *
 * @author Maxim Petrashev
 */
public interface ResourceHolder<E> {
    E get();
    void set(E aObject);
}
