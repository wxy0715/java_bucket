package com.wxy.algorithm.tree;

import java.util.TreeMap;

/** 字典树*/
public class Trie {
    public class Node{
        private boolean isWord;
        private TreeMap<Character,Node> next;

        public Node(){
            this(false);
        }
        public Node(boolean isWord){
            this.isWord = isWord;
            next = new TreeMap<>();
        }
    }
    
    private int size;
    private Node root;
    
    public int getSize(){
        return size; 
    }
    
    public boolean isEmpty(){
        return size==0;
    }
    
    public void add(String word){
        Node cur = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!cur.next.containsKey(c)){
                cur.next.put(c,new Node());
            }
            cur = cur.next.get(c);
        }

        if (!cur.isWord){
            size++;
            cur.isWord = true;
        }
    }

    public boolean contains(String word){
        Node cur = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (cur.next.containsKey(c)){
                cur = cur.next.get(c);
            } else {
                return false;
            }
        }
        return cur.isWord;
    }
}
