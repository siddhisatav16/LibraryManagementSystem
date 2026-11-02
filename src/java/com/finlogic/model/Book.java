package com.finlogic.model;

public class Book {

    private int    id;
    private String title;
    private String author;
    private String category;
    private int    quantity;

    public Book() {}

    public Book(String title, String author, String category, int quantity) {
        this.title    = title;
        this.author   = author;
        this.category = category;
        this.quantity = quantity;
    }

    public Book(int id, String title, String author, String category, int quantity) {
        this.id       = id;
        this.title    = title;
        this.author   = author;
        this.category = category;
        this.quantity = quantity;
    }

    // Getters & Setters
    public int    getId()            { return id; }
    public void   setId(int id)      { this.id = id; }

    public String getTitle()              { return title; }
    public void   setTitle(String title)  { this.title = title; }

    public String getAuthor()               { return author; }
    public void   setAuthor(String author)  { this.author = author; }

    public String getCategory()                  { return category; }
    public void   setCategory(String category)   { this.category = category; }

    public int  getQuantity()               { return quantity; }
    public void setQuantity(int quantity)   { this.quantity = quantity; }
}