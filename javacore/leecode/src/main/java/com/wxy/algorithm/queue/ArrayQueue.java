package com.wxy.algorithm.queue;


import com.wxy.algorithm.array.Array;
import lombok.SneakyThrows;

public class ArrayQueue<T extends Comparable<T>> implements Queue<T> {
    private Array<T> array;

    public ArrayQueue() {
        this.array = new Array<>();
    }

    public ArrayQueue(int capacity) {
        this.array = new Array<>(capacity);
    }

    @Override
    public int getSize() {
        return array.getPosition();
    }

    @Override
    public boolean isEmpty() {
        return array.isEmpty();
    }

    @Override
    public T getFront() throws Exception {
        return array.find(0);
    }

    @Override
    public void enqueue(T element) {
        array.addLast(element);
    }

    @Override
    public void dequeue() throws Exception {
        array.deleteByIndex(0);
    }

    @SneakyThrows
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Queue{");
        for (int i = 0; i < getSize(); i++) {
            stringBuilder.append(array.find(i));
            if (i != getSize()-1) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("}front");
        return stringBuilder.toString();
    }

    public static void main(String[] args) throws Exception {
        ArrayQueue<Integer> objectArrayStck = new ArrayQueue<>();
        for (int i = 0; i < 5; i++) {
            objectArrayStck.enqueue(i);
            System.out.println(objectArrayStck);
        }
        objectArrayStck.dequeue();
        System.out.println(objectArrayStck);
        Integer front = objectArrayStck.getFront();
        System.out.println(front);
    }
}
