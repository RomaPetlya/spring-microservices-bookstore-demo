package com.bookstore.dto;

public class OrderItemDto {
    private String id;
    private String bookId;
    private int quantity;
    private double price;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getBookId() {
        return bookId;
    }
    
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
}