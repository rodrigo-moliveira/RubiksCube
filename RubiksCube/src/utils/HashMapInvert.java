package src.utils;

import java.util.HashMap;

public class HashMapInvert<K,V> extends HashMap<K,V> {

    HashMap<V,K> reverseMap = new HashMap<>();
    @Override
    public V put(K key, V value) {
        reverseMap.put(value, key);
        return super.put(key, value);
    }

    public K getKey(V value) {
        return reverseMap.get(value);
    }
}