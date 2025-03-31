package com.example.demo.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class Cache<K, V> extends LinkedHashMap<K, V> {
    private static final int CAPACITY = 4;

    public Cache() {
        super(CAPACITY, 0.75f, true);
    }

    @Override
    public boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > CAPACITY;
    }
}
