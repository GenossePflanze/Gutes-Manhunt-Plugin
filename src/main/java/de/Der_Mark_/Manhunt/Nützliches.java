package de.Der_Mark_.Manhunt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Nützliches {
    public static <K, V> List<K> getKeysByValue(Map<K, V> map, V value) {
        List<K> keys = new ArrayList<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                keys.add(entry.getKey());
            }
        }
        if (keys.isEmpty()) {
            return new ArrayList<>();
        }
        return keys;
    }
}
