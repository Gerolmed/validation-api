package net.endrealm.validate.test.validators;

import lombok.Data;
import net.endrealm.validate.annotations.Validation;
import net.endrealm.validate.annotations.Validator;
import net.endrealm.validate.test.injectors.UserService;

/**
 *
 */
@Validator(priority = -1)
@Data
public class StringValidator {
    private final UserService userService;

    @Validation
    public boolean validate(String user) {
        System.out.println("Called other me");
        return userService.getUserNames().contains(user);
    }
}
