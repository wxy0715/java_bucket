package com.wxy.algorithm.stack;


import com.wxy.algorithm.array.Array;
import lombok.SneakyThrows;

public class ArrayStck<T extends Comparable<T>> implements Stack<T> {

    private Array<T> array;

    public ArrayStck() {
        this.array = new Array<>();
    }

    public ArrayStck(int capacity) {
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
    public T peek() throws Exception {
        return array.find(getSize()-1);
    }

    @Override
    public void push(T element) {
        array.addLast(element);
    }

    @Override
    public void pop() throws Exception {
        array.deleteByIndex(getSize()-1);
    }

    @SneakyThrows
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Stack{");
        for (int i = 0; i < getSize(); i++) {
            stringBuilder.append(array.find(i));
            if (i != getSize()-1) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("}top");
        return stringBuilder.toString();
    }

    public static void main(String[] args) throws Exception {
        ArrayStck<Integer> objectArrayStck = new ArrayStck<>();
        for (int i = 0; i < 5; i++) {
            objectArrayStck.push(i);
            System.out.println(objectArrayStck);
        }
        objectArrayStck.pop();
        System.out.println(objectArrayStck);
    }
}
