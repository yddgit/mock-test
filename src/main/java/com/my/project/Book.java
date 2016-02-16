package com.my.project;

import java.util.List;

/**
* Model class for the book details.
*/
public class Book {

    private String isbn;
    private String title;
    private List<String> authors;
    private String publication;
    private Integer yearOfPublication;
    private Integer numberOfPages;
    private String image;

    public Book(String isbn, String title, List<String> authors, String publication, Integer yearOfPublication,
            Integer numberOfPages, String image) {

        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.publication = publication;
        this.yearOfPublication = yearOfPublication;
        this.numberOfPages = numberOfPages;
        this.image = image;

    }

    public Book(String title) {
        this.title = title;
    }

    public Book() {
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getPublication() {
        return publication;
    }

    public Integer getYearOfPublication() {
        return yearOfPublication;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public String getImage() {
        return image;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public void setYearOfPublication(Integer yearOfPublication) {
        this.yearOfPublication = yearOfPublication;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /**
     * For Mock Test
     * @param isbn
     * @param title
     * @param publication
     * @return
     */
    public String setInfo(String isbn, String title, String publication) {
        this.isbn = isbn;
        this.title = title;
        this.publication = publication;
        return "success";
    }
    
    public void timeoutMethod() {
        //do nothing
    }
}