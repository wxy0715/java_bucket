package com.wxy.design.iterator;

import java.util.List;

public class BookShelfIterator implements Iterator {
    private List<Book> bookList;
    private int index;

    public BookShelfIterator(List<Book> bookList) {
        this.bookList = bookList;
        index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < bookList.size();
    }

    @Override
    public Object next() {
        Book book = bookList.get(index);
        index++;
        return book;
    }

}
