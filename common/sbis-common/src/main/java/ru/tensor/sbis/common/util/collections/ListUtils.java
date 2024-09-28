package ru.tensor.sbis.common.util.collections;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility methods for lists.
 *
 * @author am.boldinov
 */
public class ListUtils {

    /**
     * Concat elements of specified lists and return it.
     * Source lists may be nullable or empty but resulting list
     * is always not null.
     * @param lists - collection of lists to concat
     * @param <T>   - type of items
     * @return list with elements of two specified lists
     */
    @SafeVarargs
    @NonNull
    public static <T> ArrayList<T> concat(List<T>... lists) {
        int count = 0;
        for (List<T> list : lists) {
            if (list != null) {
                count += list.size();
            }
        }
        final ArrayList<T> result = new ArrayList<>(count);
        for (List<T> list : lists) {
            if (list != null) {
                result.addAll(list);
            }
        }
        return result;
    }

    /**
     * Retrieve from list items, that corresponds to the condition and returns them as list.
     * @param source    - source list
     * @param condition - condition for retrieving
     * @param <T> - type of items in list
     * @return list of retrieved items
     */
    @NonNull
    public static <T> ArrayList<T> takeWithMutate(@NonNull List<T> source, @NonNull Predicate<? super T> condition) {
        return take(source, condition, true);
    }

    /**
     * Takes from list items, that corresponds to the condition and returns them as list.
     * @param source    - source list
     * @param condition - condition for taking
     * @param <T> - type of items in list
     * @return list of taken items
     */
    @NonNull
    public static <T> ArrayList<T> takeWithoutMutate(@NonNull List<T> source, @NonNull Predicate<T> condition) {
        return take(source, condition, false);
    }

    /**
     * Take from list items, that corresponds to the condition and returns them as list.
     * @param source        - source list
     * @param condition     - condition for taking
     * @param changeSource  - if true - items will be retrieved from source list,
     *                      elsewhere source list will not be changed
     * @param <T> - type of items in list
     * @return list of taken items
     */
    @NonNull
    public static <T> ArrayList<T> take(@NonNull List<T> source, @NonNull Predicate<? super T> condition, boolean changeSource) {
        final ArrayList<T> suitable = new ArrayList<>();
        Iterator<T> iterator = source.iterator();
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (condition.apply(item)) {
                suitable.add(item);
                if (changeSource) {
                    iterator.remove();
                }
            }
        }
        return suitable;
    }

    /**
     * Leaves only those items in list, that corresponds to the condition.
     * @param list      - source list
     * @param condition - condition for filtering
     * @param <T>       - type of items in list
     * @return source list without items, that not corresponds to the condition.
     */
    @NonNull
    public static <T> List<T> filter(@NonNull List<T> list, @NonNull Predicate<T> condition) {
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (!condition.apply(item)) {
                iterator.remove();
            }
        }
        return list;
    }

    /**
     * Find densest cluster of object, that corresponds to condition, restricted by window size.
     * @param list          - source list
     * @param windowSize    - size of window for cluster
     * @param condition     - condition
     * @param <T>           - type of items in list
     * @return sublist of source list, represents densest cluster.
     */
    public static <T> ArrayList<T> searchDensestCluster(@NonNull List<T> list, int windowSize,
                                                        @NonNull Predicate<T> condition) {
        assertNonNegativeWindowSize(windowSize);
        int sourceSize = list.size();
        int bestTailIndex = -1;
        int maxMatched = 0;
        int matched = 0;

        // Заполняем очередь поиска
        final ArrayList<T> queue = new ArrayList<>(windowSize);
        for (int i = 0; i < windowSize; i++) {
            queue.add(null);
        }

        for (int i = 0; i < sourceSize; i++) {
            T element = list.get(i);
            T cur = queue.get(i % windowSize);
            // Проверяем следующий элемент
            if (condition.apply(element)) {
                if (!condition.apply(cur)) {
                    // Увеличиваем количество подходящих элементов
                    ++matched;
                }
            } else if (condition.apply(cur)) {
                // Уменьшаем количество подходящих элементов
                --matched;
            }
            // Добавляем элемент в очередь
            queue.set(i % windowSize, element);
            if (matched > maxMatched) {
                bestTailIndex = i;
                maxMatched = matched;
            }
        }

        // Вычисляем границы самого подходящего окна
        int startIndex, endIndex;
        if (bestTailIndex < windowSize) {
            // Решающая последовательность меньше окна
            startIndex = 0;
            endIndex = startIndex + Math.min(windowSize, sourceSize);
        } else {
            // Решающая последовательность равна окну
            startIndex = bestTailIndex - windowSize + 1;
            endIndex = startIndex + windowSize;
        }
        // Заполняем результат самым подходящим окном
        final ArrayList<T> result = new ArrayList<>(endIndex - startIndex);
        for (int i = startIndex; i < endIndex; i++) {
            result.add(list.get(i));
        }
        return result;
    }

    private static void assertNonNegativeWindowSize(int window) {
        if (window < 0) {
            throw new IllegalArgumentException("Window has negative size : " + window);
        }
    }

    /**
     * Returns ArrayList containing only the specified object
     * @param source - the sole object to be stored in the returned list
     * @param <T>    - the class of the objects in the list
     * @return ArrayList containing only the specified object
     */
    @NonNull
    public static <T> ArrayList<T> singletonArrayList(@NonNull T source) {
        ArrayList<T> list = new ArrayList<>(1);
        list.add(source);
        return list;
    }

}
