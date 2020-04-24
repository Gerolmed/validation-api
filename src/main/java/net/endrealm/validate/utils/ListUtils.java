package net.endrealm.validate.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class ListUtils {
    public static  <T> boolean match(Collection<T> list1, Collection<T> list2) {
        List<T> list1Clone = new ArrayList<>(list1);
        list1Clone.removeAll(list2);

        List<T> list2Clone = new ArrayList<>(list2);
        list2Clone.removeAll(list1);

        return list1Clone.isEmpty() && list2Clone.isEmpty();
    }
}
