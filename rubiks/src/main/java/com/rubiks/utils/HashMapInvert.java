package com.rubiks.utils;

import java.util.HashMap;

public class HashMapInvert<K,V> extends HashMap<K,V> {

    private static final long serialVersionUID = 1L;
	HashMap<V,K> reverseMap = new HashMap<V, K>();
    @Override
    public V put(K key, V value) {
        reverseMap.put(value, key);
        return super.put(key, value);
    }

    public K getKey(V value) {
        return reverseMap.get(value);
    }
}