package net.endrealm.validate.test.validators;

import net.endrealm.validate.annotations.Validation;
import net.endrealm.validate.annotations.Validator;

import java.util.Date;

/**
 *
 */
@Validator
public class DateValidator {

    @Validation
    public void validate(Date date) {

    }

    @Validation
    public void validate3(String date) {
    }


    @Validation
    public boolean validate2() {
        return true;
    }
}
