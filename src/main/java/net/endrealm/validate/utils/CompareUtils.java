package net.endrealm.validate.utils;

import net.endrealm.validate.tree.TreeComponent;

import java.util.Comparator;

/**
 *
 */
public class CompareUtils {
    public static Comparator<TreeComponent<?>> getCompare() {
        return CompareUtils::compare;
    }
    private static final Comparator<TreeComponent<?>> WEIGHT_COMPARE = Comparator.comparingInt(TreeComponent::getWeight);

    private static int compare(TreeComponent<?> o1, TreeComponent<?> o2) {
        int result = WEIGHT_COMPARE.compare(o1, o2);

        if(result != 0) return result;


        return o1.hashCode() - o2.hashCode();
    }
}
