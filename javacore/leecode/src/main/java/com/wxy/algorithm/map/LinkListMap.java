package com.wxy.algorithm.map;

public class LinkListMap<K,V> implements Map<K,V> {

    private class Node{
        public K key;
        public V value;
        public Node next;

        public Node(K key, V value, Node next){
            this.key = key;
            this.value = value;
            this.next = next;
         }

        public Node(K key){
            this(key,null,null);
        }

        public Node(K key, V value){
            this(key,value,null);
        }
        public Node(){
            this(null,null,null);
        }
        @Override
        public String toString(){
            return key.toString()+":"+value.toString();
        }
    }

    private Node head;
    private int size;

    public LinkListMap(){
        head = new Node();
        size = 0;
    }

    @Override
    public void add(K key, V value) {
        if (!contains(key)) {
            head.next = new Node(key,value,head.next);
        } else {
            throw new IllegalArgumentException("key存在!");
        }
    }

    @Override
    public V remove(K key) {
        if (contains(key)){
            Node cur = head;
            while(cur.next!=null){
                if (cur.next.key.equals(key)){
                    Node delNode = cur.next;
                    cur.next = delNode.next;
                    delNode.next = null;
                    size--;
                    return cur.next.value;
                }
                cur = cur.next;
            }
            return null;
        } else {
            throw new IllegalArgumentException("key不存在!");
        }
    }

    @Override
    public boolean contains(K key) {
        return getNode(key) != null;
    }

    @Override
    public V get(K key) {
        return getNode(key);
    }

    public V getNode(K key){
        Node cur = head.next;
        while(cur != null){
            if (cur.key.equals(key)) {
                return cur.value;
            }
            cur = cur.next;
        }
        return null;
    }

    @Override
    public void set(K key, V value) {
        if (!contains(key)) {
            throw new IllegalArgumentException("key不存在!");
        }
        Node cur = head.next;
        while(cur != null){
            if (cur.key.equals(key)) {
                cur.value = value;
                return;
            }
            cur = cur.next;
        }
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }
}
