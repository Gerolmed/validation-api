package net.endrealm.validate.tree;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Data
public class TreeData<T> {

    private final Map<TreeComponent<T>, ComponentData<T>> dataMap = new HashMap<>();
    private final List<Exception> exceptions = new ArrayList<>();

    public ComponentData<T> getData(TreeComponent<T> treeComponent) {
        return dataMap.computeIfAbsent(treeComponent, treeComponent1 -> new ComponentData<>());
    }

    @Data
    public static class ComponentData<T> {
        private final List<TreeComponent<T>> fullFilledParents = new ArrayList<>();
        private boolean finished;
    }
}
