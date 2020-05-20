package net.endrealm.validate.api;

import net.endrealm.validate.exceptions.ValidationException;
import net.endrealm.validate.utils.Pair;

import java.util.List;

/**
 *
 */
public interface ValidationCore {
    boolean isValidSimple(Object object);
    List<Exception> isValidEx(Object object);

    /**
     * Validates an object and returns errors of the specified
     * type, errors not of that class will be thrown
     */
    <T extends ValidationException> List<T> isValid(Object object, Class<T> errorClass) throws Exception;
    /**
     * Validates an object and returns errors of the specified
     * type, errors not of that class will be returned in the pair value
     */
    <T extends ValidationException> Pair<List<T>, List<Exception>> isValidAllEx(Object object, Class<T> errorClass);

    Pair<List<ValidationException>,List<Exception>> isValid(Object object);




    List<Exception> isValidEx(Object object, DownStreamContext initContext);

    <T extends ValidationException> List<T> isValid(Object object, Class<T> errorClass, DownStreamContext initContext) throws Exception;

    <T extends ValidationException> Pair<List<T>, List<Exception>> isValidAllEx(Object object, Class<T> errorClass, DownStreamContext initContext);

    Pair<List<ValidationException>,List<Exception>> isValid(Object object, DownStreamContext initContext);
}
