package com.wxy.algorithm.linkedlist;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LinkedList<T> {
    private class Node{
        public T e;
        public Node next;

        public Node(T e, Node next) {
            this.e = e;
            this.next = next;
        }

        public Node(T e) {
            this.e = e;
            this.next = null;
        }
        public Node() {
           this(null,null);
        }

        @Override
        public String toString() {
            return e.toString();
        }
    }
    private Node head;
    private int size;
    public LinkedList() {
        head = new Node(null,null);
        size = 0;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Node cur = head.next;
        while (cur != null) {
            stringBuilder.append(cur.e);
            stringBuilder.append("->");
            cur = cur.next;
        }
        stringBuilder.append("NULL");
        return stringBuilder.toString();
    }

    /** 在链表的头部添加新的元素*/
    public void addFirst(T element){
       add(0,element);
    }

    /** 在链表的尾部添加新的元素*/
    public void addLast(T element){
        add(size,element);
    }

    /** 在索引处添加新的元素*/
    public void add(int index, T element) {
        if (index < 0 || index > size) {
            throw new IllegalArgumentException("索引越界");
        }
        Node pre = head;
        for (int i = 0; i < index; i++) {
            pre = pre.next;
        }
        pre.next = new Node(element, pre.next);
        size++;
    }

    /** 获取索引处的元素*/
    public T getByIndex(int index){
        if (index < 0 || index > size) {
            throw new IllegalArgumentException("索引越界");
        }
        Node cur = head.next;
        for (int i = 0; i <index; i++) {
            cur = cur.next;
        }
        return cur.e;
    }

    /** 根据元素查找索引*/
    public int getFirstIndex(T element){
        Node cur = head.next;
        for (int i = 0; i < size; i++) {
            if (cur.e.equals(element)) {
                return i;
            }
            cur = cur.next;
        }
        return -1;
    }

    /** 获取第一个元素*/
    public T getFirst(){
        return getByIndex(0);
    }

    /** 获取最后一个元素*/
    public T getLast(){
        return getByIndex(size-1);
    }

    /** 修改索引处的元素*/
    public void set(int index, T element) {
        if (index < 0 || index > size) {
            throw new IllegalArgumentException("索引越界");
        }
        Node cur = head.next;
        for (int i = 0; i <index; i++) {
            cur = cur.next;
        }
        cur.e = element;
    }

    /** 修改链表的所有相同的值*/
    public void put(int index, T element) {
        if (index < 0 || index > size) {
            throw new IllegalArgumentException("索引越界");
        }
        int pos = 0;
        while((pos=getFirstIndex(element))!=-1) {
            set(pos,element);
        }
    }

    /** 查找链表是否含有元素*/
    public boolean contain(T element){
        Node cur = head.next;
        for (int i = 0; i < size; i++) {
            if (cur.e.equals(element)) {
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    /** 从index删除元素*/
    public void delete(int index) {
        if (index < 0 || index > size) {
            throw new IllegalArgumentException("索引越界");
        }
        if (index == 0) {
            Node delNode =head.next;
            head.next = delNode.next;
            delNode.next= null;
        } else {
            Node preNode = head.next;
            for (int i = 0; i < index-1; i++) {
                preNode = preNode.next;
            }
            Node delNode = preNode.next;
            preNode.next = delNode.next;
            delNode.next = null;
        }

        size--;
    }

    /** 根据值删除所有-慢效率-每次都要遍历*/
    public void removeElement(T element){
        int pos = 0;
        while((pos=getFirstIndex(element))!=-1) {
            delete(pos);
        }
    }

    /** 根据值删除所有-快效率递归*/
    public Node removeElementQuick(T element, Node node){
        if (node == null) {
            return null;
        }
        node.next = removeElementQuick(element, node.next);
        if (node.e != null && node.e.equals(element)) {
            size--;
            return node.next;
        } else {
            return node;
        }
    }

    /** 链表翻转*/
    public Node reverse(Node head){
        if(head == null || head.next == null){
            return head;
        }
        Node p1 = head;
        Node p2 = head.next;
        Node p3 = null;
        while(p2 != null) {
            p3 = p2.next;
            p2.next = p1;
            p1 = p2;
            p2 = p3;
        }
        head.next = null;
        head = p1;
        return head;
    }

    public static void main(String[] args) {
        LinkedList<Integer> list = new LinkedList<>();
        list.add(0,1);
        list.add(1,2);
        list.add(2,3);
        list.add(3,1);
        System.out.println(list);
        list.removeElementQuick(1,list.head);
        System.out.println(list);
    }
}
