package com.my.project.exclude;

import com.my.project.Book;

public class ExcludedClass {

    public void save(Book book) {
        System.out.println("Save Book!");
    }

    public void delete(String isbn) {
        System.out.println("Delete Book!");
    }

    public void update(Book book) {
        System.out.println("Update Book!");
    }
}
