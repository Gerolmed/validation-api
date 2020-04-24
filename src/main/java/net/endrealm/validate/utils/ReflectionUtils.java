package net.endrealm.validate.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * adapted from https://raw.githubusercontent.com/endrealm/RealmDrive/development/src/main/java/net/endrealm/realmdrive/utils/ReflectionUtils.java
 *
 * Various utility methods regarding reflection
 */
public class ReflectionUtils {
    /**
     * Returns all Fields public, protected, private and those that are inherited
     *
     * @param fields list to add fields to
     * @param type class to scan
     * @return the fields
     */
    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    /**
     * Retrieves all fields of a class.
     * Method searches super classes as well. Fields can be private, protected, public or package private.
     * @param type class to scan
     * @return list of fields
     */
    public static List<Field> getAllFields(Class<?> type) {
        return getAllFields(new ArrayList<>(), type);
    }

    /**
     * Finds all fields with at least one of the specified annotations.
     * Method searches super classes as well. Fields can be private, protected, public or package private.
     *
     * @param type clazz to scan
     * @param annotations list of annotations
     * @return all fields with one or more of the specified annotations
     */
    public static List<Field> getAllAnnotatedFields(Class<?> type, Class... annotations) {
        List<Field> rawFields = new ArrayList<>();
        List<Field> annotatedFields = new ArrayList<>();

        getAllFields(rawFields, type);

        for(Field field : rawFields)
            if(hasAnnotation(field, annotations))
                annotatedFields.add(field);

        return annotatedFields;
    }

    /**
     * Checks if a field has at least one of the specified annotations
     *
     * @param field field to check
     * @param annotations list of annotations
     * @return does field have one or more of the specified annotations
     */
    private static boolean hasAnnotation(Field field, Class[] annotations) {
        for(Class clazz : annotations)
            if(field.getAnnotation(clazz) != null)
                return true;
        return false;
    }


    public static List<Method> getAllMethodsAnnotated(Class<?> type, Class<? extends Annotation> annotation) {
        List<Method> rawMethods = new ArrayList<>();
        List<Method> annotatedMethods = new ArrayList<>();

        getAllMethods(rawMethods, type);

        for(Method method : rawMethods)
            if(hasAnnotation(method, annotation))
                annotatedMethods.add(method);

        return annotatedMethods;
    }

    private static void getAllMethods(List<Method> methods, Class<?> type) {
        methods.addAll(Arrays.asList(type.getDeclaredMethods()));

        if (type.getSuperclass() != null) {
            getAllMethods(methods, type.getSuperclass());
        }
    }

    private static boolean hasAnnotation(Method method, Class<? extends Annotation> annotation) {
        return method.getAnnotation(annotation) != null;
    }
}
