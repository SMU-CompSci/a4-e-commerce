package com.student_work;

import java.io.IOException;
import java.util.List;


public class CsvParser {


    public static List<Product> parseCsv(String filepath) throws IOException {
        // TODO: Read and parse the CSV file line by line
        throw new UnsupportedOperationException("Not implemented yet.");
    }


    public static Product parseLine(String line) {
        // TODO: Parse a single CSV line into a Product object
        throw new UnsupportedOperationException("Not implemented yet.");
    }


    private static List<String> splitCsvLine(String line) {
        // TODO: Split CSV line into fields while respecting quoted fields
        throw new UnsupportedOperationException("Not implemented yet.");
    }


    public static int parseProductId(String field) {
        // TODO: Parse and clean the product ID field
        throw new UnsupportedOperationException("Not implemented yet.");
    }


    public static String parseProductName(String field) {
        // TODO: Parse and clean the product name field
        throw new UnsupportedOperationException("Not implemented yet.");
    }


    public static String parseCategory(String field) {
        // TODO: Parse and clean the category field
        throw new UnsupportedOperationException("Not implemented yet.");
    }


    public static double parsePrice(String field) {
        // TODO: Parse the price field and convert to double
        throw new UnsupportedOperationException("Not implemented yet.");
    }


    public static void main(String[] args) {
        // TODO: Implement main method for testing CSV parsing
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}