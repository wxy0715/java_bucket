package com.wxy.algorithm.stack;

public interface Stack<T> {
    int getSize();
    boolean isEmpty();
    T peek() throws Exception;
    void push(T element);
    void pop() throws Exception;
}
