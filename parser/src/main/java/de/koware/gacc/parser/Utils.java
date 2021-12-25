package de.koware.gacc.parser;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

public class Utils {

    private Utils() {}

    public static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K, V>> comparingByValueDesc() {
        return (Comparator<Map.Entry<K, V>> & Serializable)
                (c1, c2) -> c2.getValue().compareTo(c1.getValue());
    }
}
