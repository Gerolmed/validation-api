package net.endrealm.validate.test.validators;

import net.endrealm.validate.annotations.Validation;
import net.endrealm.validate.annotations.Validator;
import net.endrealm.validate.api.DownStreamContext;

import java.util.Date;

/**
 *
 */
@Validator
public class DateValidator {

    @Validation
    public boolean validate(Date date, DownStreamContext context) {
        return context.get("test").isPresent();
    }


    @Validation
    public boolean validate2() {
        return true;
    }
}
