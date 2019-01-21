package club.curahq.core.util;

import com.google.common.base.*;
import java.util.*;
import com.google.common.collect.*;

@SuppressWarnings("rawtypes")
public class MapSorting {
    private static final Function EXTRACT_KEY;
    private static final Function EXTRACT_VALUE;

    public static <T, V extends Comparable<V>> List<Map.Entry<T, V>> sortedValues(final Map<T, V> map) {
        return sortedValues(map,  Ordering.natural());
    }

    @SuppressWarnings("unchecked")
	public static <T, V> List<Map.Entry<T, V>> sortedValues(final Map<T, V> map, final Comparator<V> valueComparator) {
        return (List<Map.Entry<T, V>>) Ordering.from((Comparator) valueComparator).onResultOf((Function) extractValue()).sortedCopy((Iterable) map.entrySet());
    }

    @SuppressWarnings("unchecked")
	public static <T, V> Iterable<T> keys(final List<Map.Entry<T, V>> entryList) {
        return (Iterable<T>) Iterables.transform((Iterable) entryList, (Function) extractKey());
    }

    @SuppressWarnings({ "unchecked" })
	public static <T, V> Iterable<V> values(final List<Map.Entry<T, V>> entryList) {
        return (Iterable<V>) Iterables.transform((Iterable) entryList, (Function) extractValue());
    }

    @SuppressWarnings("unchecked")
	private static <T, V> Function<Map.Entry<T, V>, T> extractKey() {
        return (Function<Map.Entry<T, V>, T>) MapSorting.EXTRACT_KEY;
    }

    @SuppressWarnings("unchecked")
	private static <T, V> Function<Map.Entry<T, V>, V> extractValue() {
        return (Function<Map.Entry<T, V>, V>) MapSorting.EXTRACT_VALUE;
    }

    static {
        EXTRACT_KEY = (Function) new Function<Map.Entry<Object, Object>, Object>() {
            public Object apply(final Map.Entry<Object, Object> input) {
                return (input == null) ? null : input.getKey();
            }
        };
        EXTRACT_VALUE = (Function) new Function<Map.Entry<Object, Object>, Object>() {
            public Object apply(final Map.Entry<Object, Object> input) {
                return (input == null) ? null : input.getValue();
            }
        };
    }
}
