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
    Pair<List<ValidationException>,List<Exception>> isValid(Object object);
}
