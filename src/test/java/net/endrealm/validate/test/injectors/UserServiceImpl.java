package net.endrealm.validate.test.injectors;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Data
public class UserServiceImpl implements UserService {
    private final List<String> userNames = Arrays.asList("Kevin", "Alfred", "Josh");
}
