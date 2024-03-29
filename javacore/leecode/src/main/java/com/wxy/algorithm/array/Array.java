package com.wxy.algorithm.array;

import java.util.Arrays;

public class Array<T> {
    private int position = 0;
    private T[] data;

    public Array(){
        this(10);
    }
    public Array(int capacity){
        this.data = (T[]) new Object[capacity];
    }
    public Array(T[] arr){
        this.data = (T[]) new Object[arr.length];
        System.arraycopy(arr,0,data,0,arr.length);
        position = arr.length-1;
    }
    /** 获取大小*/
    public int getSize(){
        return data.length;
    }

    public int getPosition(){
        return position;
    }

    /** 获取指针位置*/
    public boolean isEmpty(){
        return position == 0;
    }

    /** 在索引index后插入元素*/
    public void add(int index, T element) {
        if (position >= data.length) {
            resize(data.length*2);
        }
        for (int i = position; i > index; i--) {
            data[i] = data[i-1];
        }
        data[index] = element;
        position++;
    }

    /** 尾插*/
    public T addLast(T element) {
        if (position >= data.length) {
            resize(data.length*2);
        }
        data[position] = element;
        position++;
        return element;
    }

    /** 头插*/
    public void addFirst(T element) {
        if (position >= data.length) {
            resize(data.length*2);
        }
        for (int i = position; i > 0; i--) {
            data[i] = data[i-1];
        }
        data[0] = element;
        position++;
    }

    /** 通过索引删除元素*/
    public void deleteByIndex(int index) {
        if (index >= position) {
            throw new IllegalArgumentException("索引越界");
        }
        position--;
        for (int i = index; i < position; i++) {
            data[i] = data[i+1];
        }
        data[position] = null;
    }

    /** 删除匹配的第一个元素*/
    public void deleteFirst(T element) {
        Integer first = findFirst(element);
        if (first != -1) {
            deleteByIndex(first);
        }
        if (position < data.length/4 && data.length/2 != 0) {
            resize(data.length/2);
        }
    }

    /** 删除匹配的倒数第一个元素*/
    public void deleteLast(T element) {
        Integer last = findLast(element);
        if (last != -1) {
            deleteByIndex(last);
        }
        if (position < data.length/4 && data.length/2 != 0) {
            resize(data.length/2);
        }
    }

    /** 删除匹配的所有元素*/
    public void deleteAll(T element) {
        int index = -1;
        while ((index=findFirst(element)) != -1) {
            deleteByIndex(index);
        }
        if (position <= data.length/4 && data.length/2 != 0) {
            resize(data.length/2);
        }
    }

    /** 修改索引位置元素*/
    public void put(int index, T element) {
        if (index >= position) {
            throw new IllegalArgumentException("索引越界");
        }
        data[index] = element;
    }

    /** 替换所有元素*/
    public void putAll(T oldElement, T newElement) {
        int index = -1;
        while ((index=findFirst(oldElement)) != -1) {
            put(index,newElement);
        }
    }

    /** 查找匹配元素的第一个*/
    public Integer findFirst(T element) {
        if (position > 0) {
            for (int i = 0; i < position; i++) {
                if (data[i].equals(element)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /** 查找匹配元素的最后一个*/
    public Integer findLast(T element) {
        if (position > 0) {
            for (int i = position; i > 0; i--) {
                if (data[i-1].equals(element)) {
                    return i-1;
                }
            }
        }
        return -1;
    }

    /** 根据索引查找元素*/
    public T find(int index){
        if (index >= position) {
            throw new IllegalArgumentException("索引越界");
        }
        return data[index];
    }

    /** 扩缩容*/
    public void resize(int size) {
        T[] newData = (T[])new Object[size];
        if (position >= 0) {
            System.arraycopy(data, 0, newData, 0, position);
        }
        data = newData;
    }

    @Override
    public String toString() {
        return "Array{" +
                "position=" + position +
                ", data=" + Arrays.toString(data) +
                '}';
    }

    public static void main(String[] args) {
        Array<Integer> studentArray = new Array<>(4);
        // 添加元素
        studentArray.addFirst(1);
        studentArray.addLast(2);
        studentArray.addLast(1);
        studentArray.add(3,2);
        System.out.println(studentArray);
        // 修改元素
        studentArray.putAll(2,3);
        System.out.println(studentArray);
        // 扩容操作
        studentArray.add(4,4);
        System.out.println(studentArray);
        // 缩容操作
        studentArray.deleteAll(1);
        studentArray.deleteAll(4);
        System.out.println(studentArray);
    }
}
