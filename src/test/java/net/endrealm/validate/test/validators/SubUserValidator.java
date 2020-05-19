package net.endrealm.validate.test.validators;

import lombok.Data;
import net.endrealm.validate.annotations.Validation;
import net.endrealm.validate.annotations.Validator;
import net.endrealm.validate.api.DownStreamContext;
import net.endrealm.validate.test.injectors.UserService;

/**
 *
 */
@Validator(dependsOn = StringValidator.class)
@Data
public class SubUserValidator {

    @Validation
    public boolean validate(DownStreamContext context, String user) {

        UserService userService = context.get("service", UserService.class).get();

        return userService.getUserNames().contains(user);
    }
}
