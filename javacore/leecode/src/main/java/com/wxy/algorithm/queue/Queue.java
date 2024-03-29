package com.wxy.algorithm.queue;

public interface Queue<T> {
    int getSize();
    boolean isEmpty();
    void enqueue(T element);
    void dequeue() throws Exception;
    T getFront() throws Exception;
}
