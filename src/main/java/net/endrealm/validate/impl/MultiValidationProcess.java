package net.endrealm.validate.impl;

import lombok.*;
import net.endrealm.validate.api.DownStreamContext;
import net.endrealm.validate.api.ValidationProcess;
import net.endrealm.validate.exceptions.ValidationException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class MultiValidationProcess implements ValidationProcess<Object> {
    private final Class<?> container;
    private final List<SimpleValidationProcess<Object>> methods;
    private Object containerObject;

    public MultiValidationProcess(Class<?> container, List<SimpleValidationProcess<Object>> methods) {
        this.container = container;
        this.methods = methods; // methods.stream().map(method -> new SimpleValidationProcess<>(container, method)).collect(Collectors.toList())
    }

    @Override
    public void validate(Object object, DownStreamContext downStreamContext) throws Exception {
        for (SimpleValidationProcess<Object> process : methods) {
            if(process.supports(object.getClass())){
                process.validate(object, downStreamContext);
            }
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    public void setContainerObject(Object containerObject) {
        for (SimpleValidationProcess<Object> process : methods) {
            process.setContainerObject(containerObject);
        }
    }
}
