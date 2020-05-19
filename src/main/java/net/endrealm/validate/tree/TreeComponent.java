package net.endrealm.validate.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.endrealm.validate.api.DownStreamContext;
import net.endrealm.validate.impl.ExecutionData;
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

    public boolean ifPresent(ConsumerThrow<ExecutionData<T>> onValue, DownStreamContext downStreamContext) throws Exception {
        if(isEmpty()) return false;
        onValue.accept(new ExecutionData<>(value, downStreamContext));
        return true;
    }

    public boolean ifPresentValue(ConsumerThrow<T> onValue) throws Exception {
        if(isEmpty()) return false;
        onValue.accept(value);
        return true;
    }

    public void foreach(ConsumerThrow<ExecutionData<T>> consumer, TreeData<T> treeData) {
        foreach(consumer, treeData, null);
    }

    public void foreach(ConsumerThrow<ExecutionData<T>> consumer, TreeData<T> treeData, TreeComponent<T> parent) {
        TreeData.ComponentData<T> data = treeData.getData(this);

        if(parent != null)
            data.getFullFilledParents().add(parent);

        if(!ListUtils.match(parents, data.getFullFilledParents()))
            return;

        boolean errored = false;

        DownStreamContext downStreamContext = data.collectDownStream(treeData);
        try {
            if(!data.isFinished())
                ifPresent(consumer, downStreamContext);
        } catch (Exception ex) {
            treeData.getExceptions().add(ex);
            errored = true;
        }
        data.setFinished(true);

        if(errored)
            return;

        for (TreeComponent<T> child: children) {
            child.foreach(consumer, treeData, this);
        }
    }

    public interface ConsumerThrow<T> {
        void accept(T value) throws Exception;
    }
}
