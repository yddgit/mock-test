package com.my.project;

import java.util.Collections;
import java.util.List;

/**
* API layer for persisting and retrieving the Book objects.
*/
public class BookDAL {

    private static BookDAL bookDAL = new BookDAL();
    
    private BookDAL() {
    }
    
    private Book book;
    
    public BookDAL(Book book) {
        this.book = book;
    }
    
    public void changeSampleTitle() {
        this.book.setTitle("INJECTED");
    }
    
    public Book getSampleBook() {
        return this.book;
    }

    public List<Book> getAllBooks() {
        return Collections.emptyList();
    }

    public Book getBook(String isbn) {
        return null;
    }

    public String addBook(Book book) {
        return book.getIsbn();
    }

    public String updateBook(Book book) {
        return book.getIsbn();
    }
    
    public void setBookTitle(Book book, String title) {
        if(book != null) {
            book.setTitle(title);
        }
    }
    
    public String getBookTitleByIsbn(String isbn) {
        return "book title";
    }

    public static BookDAL getInstance() {
        return bookDAL;
    }

    /**
     * @param book the book to set
     */
    public void setBook(Book book) {
        this.book = book;
    }
    
}