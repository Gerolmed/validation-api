package net.endrealm.validate.impl;

import lombok.Data;
import net.endrealm.validate.api.Injection;
import net.endrealm.validate.api.ValidationCore;
import net.endrealm.validate.api.ValidationProcess;
import net.endrealm.validate.exceptions.SkipValidate;
import net.endrealm.validate.exceptions.ValidationException;
import net.endrealm.validate.tree.TreeComponent;
import net.endrealm.validate.tree.TreeData;
import net.endrealm.validate.utils.CompareUtils;
import net.endrealm.validate.utils.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;

/**
 *
 */
@Data
public class ValidationCoreImpl implements ValidationCore {

    private static final Logger LOGGER = Logger.getLogger(ValidationCoreImpl.class.getSimpleName());


    private final Collection<TreeComponent<ValidationProcess<?>>> validationTrees;
    private final Set<TreeComponent<ValidationProcess<?>>> roots;
    private final Map<String, Object> beans;

    // will later be used to inject as method parameters (not added)
    private final List<Injection<?>> injections;

    public ValidationCoreImpl(Collection<TreeComponent<ValidationProcess<?>>> validationTrees, List<Injection<?>> injections) {
        this.validationTrees = validationTrees;
        this.injections = injections;

        beans = new HashMap<>();
        roots = new TreeSet<>(CompareUtils.getCompare());
        selectRoots();

        buildBeans();
        LOGGER.info("Successfully created validation core!");
    }

    private void selectRoots() {
        roots.clear();
        for (TreeComponent<ValidationProcess<?>> component : validationTrees) {
            if(component.isRoot())
                roots.add(component);
        }
    }

    private void buildBeans() {
        beans.clear();
        for(TreeComponent<ValidationProcess<?>> component : validationTrees) {
            if(component.isEmpty())
                continue;


            Class<?> type = component.getValue().getContainer();
            String key = type.getName();

            Constructor<?> constructor = type.getConstructors()[0];
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] paramValues = new Object[paramTypes.length];

            int i = 0;
            for(Class<?> paramType : paramTypes) {
                Object value = getInjection(paramType);

                if(value == null) {
                    LOGGER.severe(String.format("Could not find a suitable object to inject for %s of Validation Bean %s)", paramType.getName(), key));
                    LOGGER.severe("Shutting down to prevent any harm!");
                    System.exit(1);
                }
                paramValues[i++] = value;
            }
            try {
                Object instance = constructor.newInstance(paramValues);
                beans.put(key, instance);
                component.ifPresentValue(validationProcess -> validationProcess.setContainerObject(instance));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Object getInjection(Class<?> paramType) {
        for (Injection<?> injection: getInjections()) {
            Object value = injection.get(paramType);
            if(value != null)
                return value;
        }
        return null;
    }

    @Override
    public boolean isValidSimple(Object object) {
        return isValidEx(object).isEmpty();
    }

    @Override
    public List<Exception> isValidEx(Object object) {
        List<Exception> errors = new ArrayList<>();
        for(TreeComponent<ValidationProcess<?>> root : roots) {
            TreeData<ValidationProcess<?>> treeData = new TreeData<>();

            root.foreach( (executionData) -> {
                ValidationProcess<?> validator = executionData.getValue();
                if(validator.supports(object.getClass()))
                    //noinspection unchecked,rawtypes,rawtypes
                    ((ValidationProcess) validator).validate(object, executionData.getDownStreamContext());
            }, treeData);

            errors.addAll(treeData.getExceptions());
        }
        errors.removeIf(e -> SkipValidate.class.isAssignableFrom(e.getClass()));
        return errors;
    }


    @Override
    public <T extends ValidationException> List<T> isValid(Object object, Class<T> errorClass) throws Exception {
        Pair<List<T>, List<Exception>> result = isValidAllEx(object, errorClass);

        if(!result.getValue().isEmpty())
            throw result.getValue().get(0);
        return result.getKey();
    }

    @Override
    public <T extends ValidationException> Pair<List<T>, List<Exception>> isValidAllEx(Object object, Class<T> errorClass) {
        List<Exception> errors = isValidEx(object);
        List<T> validationExceptions = new ArrayList<>();
        for (Exception ex : new ArrayList<>(errors)) {
            if(!(errorClass.isAssignableFrom(ex.getClass()))) {
                continue;
            }
            errors.remove(ex);
            //noinspection unchecked
            validationExceptions.add((T) ex);
        }

        return Pair.of(validationExceptions, errors);
    }

    @Override
    public Pair<List<ValidationException>, List<Exception>> isValid(Object object) {
        List<Exception> errors = isValidEx(object);
        List<ValidationException> validationExceptions = new ArrayList<>();
        for (Exception ex : new ArrayList<>(errors)) {
            if(!(ex instanceof ValidationException)) {
                continue;
            }
            errors.remove(ex);
            validationExceptions.add((ValidationException) ex);
        }

        return Pair.of(validationExceptions, errors);
    }
}
