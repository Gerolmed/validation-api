package net.endrealm.validate.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.endrealm.validate.utils.CompareUtils;
import net.endrealm.validate.utils.ListUtils;

import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 *
 */
@Data
public class TreeComponent<T> {

    @ToString.Exclude
    private final Set<TreeComponent<T>> parents = new TreeSet<>(CompareUtils.getCompare());
    @EqualsAndHashCode.Exclude
    private final Set<TreeComponent<T>> children = new TreeSet<>(CompareUtils.getCompare());
    private final int weight;
    private T value;

    public void addChild(TreeComponent<T> child) {
        children.add(child);
    }
    public void addParent(TreeComponent<T> parent) {
        parents.add(parent);
    }

    public boolean isRoot() {
        return parents.isEmpty();
    }

    public boolean isEnd() {
        return children.isEmpty();
    }

    public boolean isEmpty() {
        return value == null;
    }

    public boolean ifPresent(Consumer<T> onValue) {
        if(isEmpty()) return false;
        onValue.accept(value);
        return true;
    }

    public void foreach(Consumer<T> consumer, TreeData<T> treeData) {
        foreach(consumer, treeData, null);
    }

    public void foreach(Consumer<T> consumer, TreeData<T> treeData, TreeComponent<T> parent) {
        TreeData.ComponentData<T> data = treeData.getData(this);

        if(parent != null)
            data.getFullFilledParents().add(parent);

        if(!ListUtils.match(parents, data.getFullFilledParents()))
            return;

        boolean errored = false;
        try {
            ifPresent(consumer);
        } catch (Exception ex) {
            treeData.getExceptions().add(ex);
            errored = true;
        }

        if(errored)
            return;

        for (TreeComponent<T> child: children) {
            child.foreach(consumer, treeData, this);
        }
    }
}
