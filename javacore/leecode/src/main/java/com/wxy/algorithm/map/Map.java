package com.wxy.algorithm.map;

public interface Map<K,V> {
    void add(K key, V value);
    V remove(K key);
    boolean contains(K key);
    V get(K key);
    void set(K key,V value);
    int getSize();
    boolean isEmpty();
}
