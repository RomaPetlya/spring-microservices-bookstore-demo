package com.bookstore.dto;

public class BookDto {
    private String id;
    private String title;
    private String authorId;
    private int quantityAvailable;
    private float price;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
    
    public int getQuantityAvailable() {
        return quantityAvailable;
    }
    
    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }
    
    public float getPrice() {
        return price;
    }
    
    public void setPrice(float price) {
        this.price = price;
    }
}