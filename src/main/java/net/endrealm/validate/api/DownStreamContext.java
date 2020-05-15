package net.endrealm.validate.api;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 */
@Getter
public class DownStreamContext {
    private final Map<String, Object> inherited;
    private final Map<String, Object> newDefined;

    public DownStreamContext(Map<String, Object> inherited) {
        this.inherited = Collections.unmodifiableMap(inherited);
        this.newDefined = new HashMap<>();
    }

    private DownStreamContext(Map<String, Object> mergedInherit, Map<String, Object> mergedDefined) {
        this.inherited = mergedInherit;
        this.newDefined = mergedDefined;
    }

    public Object getValue(String key) {
        if(newDefined.containsKey(key))
            return newDefined.get(key);

        return inherited.get(key);
    }

    public Optional<Object> get(String key) {
        return Optional.of(getValue(key));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public <T> T getValue(String key, Class<T> tClass) {
        return get(key, tClass).get();
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> tClass) {
        Object raw = getValue(key);

        if(raw == null || tClass.isAssignableFrom(raw.getClass()))
            return Optional.empty();

        return Optional.of((T) raw);
    }

    public DownStreamContext getSubStream() {
        Map<String, Object> merged = new HashMap<>(inherited);
        merged.putAll(newDefined);
        return new DownStreamContext(merged);
    }

    public DownStreamContext merge(DownStreamContext... contexts) {
        if(contexts == null)
            return null;
        Map<String, Object> mergedInherit = new HashMap<>(inherited);
        Map<String, Object> mergedDefined= new HashMap<>(newDefined);

        for(DownStreamContext context : contexts) {
            mergedInherit.putAll(context.getInherited());
            mergedDefined.putAll(context.getNewDefined());
        }

        return new DownStreamContext(mergedInherit, mergedDefined);
    }
}
