package net.endrealm.validate.test.validators;

import lombok.Data;
import net.endrealm.validate.annotations.Validation;
import net.endrealm.validate.annotations.Validator;
import net.endrealm.validate.test.injectors.UserService;

/**
 *
 */
@Validator(dependsOn = StringValidator.class)
@Data
public class SubUserValidator {
    private final UserService userService;

    @Validation
    public boolean validate(String user) {
        return user.equals("Kevin");
    }
}
