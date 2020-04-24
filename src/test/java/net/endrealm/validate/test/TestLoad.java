package net.endrealm.validate.test;

import net.endrealm.validate.api.ValidationCore;
import net.endrealm.validate.factories.ValidationCoreFactory;
import net.endrealm.validate.factories.ValidationSettings;
import net.endrealm.validate.test.injectors.UserServiceImpl;
import org.junit.Test;

/**
 *
 */
public class TestLoad {

    @Test
    public void testLoad() {
        ValidationCoreFactory factory = new ValidationCoreFactory();

        factory.autoInject(new UserServiceImpl());
        ValidationCore core = factory.createCore(ValidationSettings.builder().addPaths("net.endrealm.validate.test.validators").build());
        String[] names = {"Agatha", "James", "Kevin", "Alfred","Henry"};
        for (String name: names) {
            System.out.printf("%s: %s\n", name, core.isValidSimple(name)+"");
            for (Exception ex : core.isValidEx(name)) {
                ex.printStackTrace();
            }
        }
    }
}
