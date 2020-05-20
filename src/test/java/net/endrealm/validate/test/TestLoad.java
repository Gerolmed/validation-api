package net.endrealm.validate.test;

import net.endrealm.validate.api.DownStreamContext;
import net.endrealm.validate.api.ValidationCore;
import net.endrealm.validate.factories.ValidationCoreFactory;
import net.endrealm.validate.factories.ValidationSettings;
import net.endrealm.validate.test.injectors.UserServiceImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class TestLoad {

    @Test
    public void testLoad() {
        ValidationCoreFactory factory = new ValidationCoreFactory();

        List<String> validNames = Arrays.asList("Jack", "Whitechapel Murderer", "Leather Apron");
        List<String> allNames = new ArrayList<>(Arrays.asList("Agatha", "James", "Kevin", "Alfred","Henry"));
        allNames.addAll(validNames);

        factory.autoInject(new UserServiceImpl(validNames));

        ValidationCore core = factory.createCore(ValidationSettings.builder().addPaths("net.endrealm.validate.test.validators").build());


        for (String name: allNames) {
            assertEquals(String.format("Name %s is not validated correctly", name), validNames.contains(name), core.isValidSimple(name));
        }
    }


    @Test
    public void testInitStream() {
        ValidationCoreFactory factory = new ValidationCoreFactory();

        List<String> validNames = Arrays.asList("Jack", "Whitechapel Murderer", "Leather Apron");
        List<String> allNames = new ArrayList<>(Arrays.asList("Agatha", "James", "Kevin", "Alfred","Henry"));
        allNames.addAll(validNames);

        factory.autoInject(new UserServiceImpl(validNames));

        ValidationCore core = factory.createCore(ValidationSettings.builder().addPaths("net.endrealm.validate.test.validators").build());

        String test = "go";
        DownStreamContext downStreamContext = new DownStreamContext();
        downStreamContext.put("test", test);
        Date date = new Date();

        assertEquals(String.format("Name %s is not validated correctly", date), true, core.isValidEx(date, downStreamContext).isEmpty());

    }
}
