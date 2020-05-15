package net.endrealm.validate.impl;

import lombok.Data;
import net.endrealm.validate.api.ValidationProcess;
import net.endrealm.validate.exceptions.ValidationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 */
@Data
public class SimpleValidationProcess<T> implements ValidationProcess<T> {

    private final Class<?> container;
    private final Method method;
    private Object containerObject;

    @Override
    public void validate(T object) throws Exception {
        try {
            boolean access = method.isAccessible();
            method.setAccessible(true);

            Object value = method.invoke(containerObject, object);

            if(((Boolean) false).equals(value) || (value == null && method.getReturnType() != void.class))
                throw new ValidationException(String.format("Method %s failed to validate %s", container.getName() +"::"+method.getName(), object.toString()));

            method.setAccessible(access);

        } catch (SecurityException | IllegalAccessException ignored) { }
        catch (InvocationTargetException ex) {
            throw (Exception) ex.getTargetException();
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return method.getParameters()[0].getType().isAssignableFrom(aClass);
    }
}
