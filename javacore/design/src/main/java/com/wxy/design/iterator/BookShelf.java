package com.wxy.design.iterator;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookShelf implements Aggregate {

    private List<Book> bookList;

    public BookShelf(){
        bookList = new ArrayList<>(16);
    }

    public void add(Book book){
        bookList.add(book);
    }

    @Override
    public Iterator iterator() {
        return new BookShelfIterator(bookList);
    }
}
