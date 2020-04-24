package net.endrealm.validate.factories;

import net.endrealm.validate.annotations.Validation;
import net.endrealm.validate.annotations.Validator;
import net.endrealm.validate.api.Injection;
import net.endrealm.validate.api.ValidationProcess;
import net.endrealm.validate.impl.MultiValidationProcess;
import net.endrealm.validate.impl.SimpleValidationProcess;
import net.endrealm.validate.impl.ValidationCoreImpl;
import net.endrealm.validate.tree.TreeComponent;
import net.endrealm.validate.utils.Pair;
import net.endrealm.validate.utils.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 */
public class ValidationCoreFactory {

    private static final Logger LOGGER = Logger.getLogger(ValidationCoreFactory.class.getSimpleName());
    private static final Level DEBUG = Level.INFO;//Level.FINER;

    private List<Injection<?>> injections = new ArrayList<>();

    public ValidationCoreImpl createCore(ValidationSettings settings) {
        Collection<TreeComponent<ValidationProcess<?>>> validationTrees = loadTrees(settings);
        return new ValidationCoreImpl(validationTrees, new ArrayList<>(injections));
    }

    private Collection<TreeComponent<ValidationProcess<?>>> loadTrees(ValidationSettings settings) {
        Set<Class<?>> classes = collect(settings);
        List<TreeComponent<ValidationProcess<?>>> tree =
                buildTree(
                        classes.stream().map(this::convert).collect(Collectors.toMap(Pair::getKey, Pair::getValue))
                );

        //TODO: find cycles and stop program if so
        LOGGER.warning("Cycle detection has not yet been added. If a cycle is in the system it will stop functioning!");

        return tree;
    }

    private Pair<Class<?>, Validator> convert(Class<?> e) {
        return Pair.from(e, v->v, v-> v.getAnnotation(Validator.class));
    }


    /**
     * Builds up a simple tree depending on the Validator annotation
     * @param data the data for the tree
     * @return list of <b>all</b> tree nodes
     */
    private List<TreeComponent<ValidationProcess<?>>> buildTree(Map<Class<?>, Validator> data) {
        Map<Class<?>, TreeComponent<ValidationProcess<?>>> treeComponentMap = new HashMap<>();

        for (Map.Entry<Class<?>, Validator> pair : data.entrySet()) {
            treeComponentMap.putIfAbsent(pair.getKey(), new TreeComponent<>(pair.getValue().priority()));
            TreeComponent<ValidationProcess<?>> component = treeComponentMap.get(pair.getKey());

            for (Class<?> parent : pair.getValue().dependsOn()) {
                Validator parentValidator = data.get(parent);
                treeComponentMap.putIfAbsent(parent, new TreeComponent<>(parentValidator.priority()));
                TreeComponent<ValidationProcess<?>> parentComponent = treeComponentMap.get(parent);


                parentComponent.addChild(component);
                component.addParent(parentComponent);
            }

            loadComponentValue(component, pair);
        }

        return new ArrayList<>(treeComponentMap.values());
    }

    /**
     * Sets the validation process inside the tree component
     *
     * @param component component to set the value
     * @param pair validator to add
     */
    private void loadComponentValue(TreeComponent<ValidationProcess<?>> component, Map.Entry<Class<?>, Validator> pair) {
        List<Method> methods = ReflectionUtils.getAllMethodsAnnotated(pair.getKey(), Validation.class);

        methods.removeIf(method -> {
            if(method.getParameterCount() != 1) {
                LOGGER.warning(String.format("Method %s does not have exactly one parameter and is going to be skipped!", pair.getKey().getName() +"::"+method.getName()));
                return true;
            }
            return false;
        });

        if(methods.isEmpty()) {
            LOGGER.warning(String.format("Validator class %s does not have a single validation method!", pair.getKey().getName()));
            return;
        }
        if(methods.size() == 1) {
            LOGGER.log(DEBUG, String.format("Single validate mode for %s", pair.getKey().getName()));
            component.setValue(new SimpleValidationProcess<>(pair.getKey(), methods.get(0)));
            return;
        }
        LOGGER.log(DEBUG, String.format("Multi validate mode for %s", pair.getKey().getName()));
        component.setValue(new MultiValidationProcess(pair.getKey(), methods));
    }

    private Set<Class<?>> collect(ValidationSettings settings) {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        // Setup paths to scan
        settings.getPaths().forEach(path -> builder.setUrls(ClasspathHelper.forPackage(path)));

        builder.setScanners(new SubTypesScanner(false),
                new TypeAnnotationsScanner())
                .filterInputsBy(new FilterBuilder()); //todo: add blacklist here

        Reflections reflections = new Reflections(builder);
        return reflections.getTypesAnnotatedWith(Validator.class);
    }

    public ValidationCoreFactory registerInject(Injection<?> injection) {
        injections.add(injection);
        return this;
    }

    public ValidationCoreFactory autoInject(Object object) {
        injections.add(tClass -> tClass.isAssignableFrom(object.getClass()) ? object : null);
        return this;
    }
}
