package com.wxy.design.iterator;

/**
 * @author wxy
 * @description Iterator模式
 * @date 2022/3/3 13:31
 */
public class IteratorMain {
    public static void main(String[] args) {
        BookShelf bookShelf = new BookShelf();
        bookShelf.add(new Book("test"));
        bookShelf.add(new Book("test1"));
        bookShelf.add(new Book("test2"));
        Iterator iterator = bookShelf.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            System.out.println(next);
        }
    }
}
