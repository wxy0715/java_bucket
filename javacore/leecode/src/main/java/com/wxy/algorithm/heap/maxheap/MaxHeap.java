package com.wxy.algorithm.heap.maxheap;



import com.wxy.algorithm.array.Array;

import java.util.Random;

/** 堆排序 全部为nlog2n*/
public class MaxHeap<T extends Comparable<T>> {
    private Array<T> array;

    public MaxHeap(){
        this(10);
    }
    public MaxHeap(T[] arr){
        array = new Array<>(arr);
        for (int i = parent(arr.length-1); i >=0 ; i--) {
            siftDown(i);
        }
    }
    public MaxHeap(int capacity){
        array = new Array<T>(capacity);
    }

    public Array<T> getArray() {
        return array;
    }

    @Override
    public String toString() {
        return "MaxHeap{" +
                "array=" + array +
                '}';
    }

    public int size(){
        return array.getPosition();
    }

    public boolean isEmpty(){
        return array.isEmpty();
    }

    /** 返回二叉树的父节点的索引*/
    public int parent(int index) {
        if (index == 0) {
            throw new IllegalArgumentException("索引越界");
        }
        return (index-1)/2;
    }

    /** 返回左孩子节点索引*/
    public int leftChild(int index) {
        return index*2+1;
    }

    /** 返回右孩子节点索引*/
    public int rightChild(int index) {
        return index*2+2;
    }

    public void add(T e){
        array.addLast(e);
        siftUp(array.getPosition()-1);
    }

    private void siftUp(int k) {
        while (k>0 && array.find(k).compareTo(array.find(parent(k)))>0){
            swap(k,parent(k));
            k = parent(k);
        }
    }


    public T findMax(){
        return array.find(0);
    }

    /** 替换堆顶元素*/
    public T replace(T e) {
        T ret = findMax();
        array.put(0,e);
        siftDown(0);
        return ret;
    }

    /** 取出堆中最大元素*/
    public T extractMax(){
        T ret =findMax();
        swap(0,size()-1);
        array.deleteByIndex(size()-1);
        siftDown(0);
        return ret;
    }

    private void siftDown(int index) {
        while(leftChild(index) < size()){
           int j = leftChild(index);
           if (j+1<size() && array.find(j).compareTo(array.find(j+1))<0){
               j += 1;
           }
           if (array.find(index).compareTo(array.find(j))>=0){
               break;
           }
           swap(j,index);
           index = j;
        }
    }

    private void siftDown1(int index,int length) {
        while(leftChild(index) < length){
            int j = leftChild(index);
            if (j+1<length && array.find(j).compareTo(array.find(j+1))<0){
                j += 1;
            }
            if (array.find(index).compareTo(array.find(j))>=0){
                break;
            }
            swap(j,index);
            index = j;
        }
    }

    public void swap(int a,int b){
        T tmp = array.find(b);
        array.put(b,array.find(a));
        array.put(a,tmp);
    }

    /** 排序优化*/
    public void sort(){
        int index = size()-1;
        for (int i = index; i >=0; i--) {
            // 先和最后一个进行交换
            swap(0,i);
            // 在进行siftDown
            siftDown1(0,i);
        }
    }

    public static void main(String[] args) {
        MaxHeap<Integer> maxHeap = new MaxHeap<>();
        int n = 100;
        for (int i = 0; i < n; i++) {
            maxHeap.add(new Random().nextInt(Integer.MAX_VALUE));
        }
        // 普通排序
//        Integer[] arr = new Integer[n];
//        for (int i = 0; i < n; i++) {
//            arr[i] = maxHeap.extractMax();
//        }
        //优化排序 空间为O(1)
        maxHeap.sort();
        for (int i = 0; i < n; i++) {
            System.out.println(maxHeap.array.find(i));
        }
        System.out.println();
        /*for (int i = 1; i < n; i++) {
            if (arr[i-1]<arr[i]){
                System.out.println(arr[i-1]);
                System.out.println(arr[i]);
                throw new IllegalArgumentException("不是降序的");
            }
        }*/
    }
}
