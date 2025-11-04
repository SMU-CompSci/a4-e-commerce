package com.student_work;


public class Product implements Comparable<Product> {
    private final String id;
    private final String name;
    private final String category;  // Multiple categories separated by |
    private final double price;

    public Product(String id, String name, String category, double price) {
        // TODO: Initialize all final fields with the provided parameters
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public String getId() {
        // TODO: Return the product ID
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public String getName() {
        // TODO: Return the product name
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public String getCategory() {
        // TODO: Return the category string (may contain multiple categories separated by |)
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public double getPrice() {
        // TODO: Return the product price
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public String toString() {
        // TODO: Return a formatted string representation of the product
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean equals(Object obj) {
        // TODO: Implement equals method to compare two Product objects
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int compareTo(Product o) {
        // TODO: Implement comparison based on product ID for Red-Black tree ordering
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}