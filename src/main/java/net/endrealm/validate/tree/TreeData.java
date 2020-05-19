package net.endrealm.validate.tree;

import lombok.Data;
import net.endrealm.validate.api.DownStreamContext;

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
        private DownStreamContext downStreamContext;
        private boolean finished;

        public DownStreamContext collectDownStream(TreeData<T> treeData) {

            this.downStreamContext = new DownStreamContext()
                    .merge(
                            fullFilledParents.stream()
                                    .map(
                                            parent -> treeData.getData(parent).getDownStreamContext()
                                    ).toArray(DownStreamContext[]::new)
                    ).getSubStream();

            return this.downStreamContext;
        }
    }
}
