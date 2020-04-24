package net.endrealm.validate.api;

import net.endrealm.validate.exceptions.ValidationException;

/**
 *
 */
public interface ValidationProcess<T> {
    Class<?> getContainer();
    void validate(T object) throws ValidationException;

    boolean supports(Class<?> aClass);
    void setContainerObject(Object containerObject);
}
