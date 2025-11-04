import com.student_work.Product;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Product class.
 * Tests all getters, toString, and equals methods according to Assignment 4 requirements.
 */
class ProductTest {

    // Test data constants matching README examples
    private static final String PRODUCT_ID_1 = "1001";
    private static final String PRODUCT_NAME_1 = "Wireless Mouse";
    private static final String CATEGORY_1 = "Electronics|Accessories";
    private static final double PRICE_1 = 29.99;

    private static final String PRODUCT_ID_2 = "1002";
    private static final String PRODUCT_NAME_2 = "Apple iPhone 14, 128GB";
    private static final String CATEGORY_2 = "Smartphones|Electronics";
    private static final double PRICE_2 = 999.99;

    private static final String PRODUCT_ID_3 = "1003";
    private static final String PRODUCT_NAME_3 = "Book: \"The Art of War\"";
    private static final String CATEGORY_3 = "Books|Classics";
    private static final double PRICE_3 = 14.99;

    private static final String PRODUCT_ID_5 = "1005";
    private static final String PRODUCT_NAME_5 = "Sony 65\" OLED TV, Smart";
    private static final String CATEGORY_5 = "Electronics|Home Entertainment|TVs";
    private static final double PRICE_5 = 1899.99;

    /**
     * README Example Product 1:
     * ID: 1001, Name: Wireless Mouse, Category: Electronics|Accessories, Price: 29.99
     */
    @Test
    void readmeExample_product1_simpleUnquotedFields() {
        Product product = new Product(PRODUCT_ID_1, PRODUCT_NAME_1, CATEGORY_1, PRICE_1);

        assertNotNull(product, "Product should be created successfully");
        assertEquals(PRODUCT_ID_1, product.getId(), "Product ID should match");
        assertEquals(PRODUCT_NAME_1, product.getName(), "Product name should match");
        assertEquals(CATEGORY_1, product.getCategory(), "Category should match");
        assertEquals(PRICE_1, product.getPrice(), 0.001, "Price should match");
    }

    /**
     * README Example Product 2:
     * Product name contains comma: "Apple iPhone 14, 128GB"
     */
    @Test
    void readmeExample_product2_nameWithComma() {
        Product product = new Product(PRODUCT_ID_2, PRODUCT_NAME_2, CATEGORY_2, PRICE_2);

        assertEquals(PRODUCT_ID_2, product.getId(), "Product ID should match");
        assertEquals(PRODUCT_NAME_2, product.getName(), "Product name with comma should be preserved");
        assertEquals(CATEGORY_2, product.getCategory(), "Category should match");
        assertEquals(PRICE_2, product.getPrice(), 0.001, "Price should match");
    }

    /**
     * README Example Product 3:
     * Product name contains escaped quotes: Book: "The Art of War"
     * Original CSV: "Book: ""The Art of War"""
     */
    @Test
    void readmeExample_product3_nameWithQuotes() {
        Product product = new Product(PRODUCT_ID_3, PRODUCT_NAME_3, CATEGORY_3, PRICE_3);

        assertEquals(PRODUCT_ID_3, product.getId(), "Product ID should match");
        assertEquals(PRODUCT_NAME_3, product.getName(), "Product name with quotes should be unescaped");
        assertTrue(product.getName().contains("\""), "Product name should contain actual quote character");
        assertEquals(CATEGORY_3, product.getCategory(), "Category should match");
        assertEquals(PRICE_3, product.getPrice(), 0.001, "Price should match");
    }

    /**
     * README Example Product 5:
     * Product name contains both comma and quote: Sony 65" OLED TV, Smart
     * Multiple categories with pipes: Electronics|Home Entertainment|TVs
     */
    @Test
    void readmeExample_product5_complexNameAndCategories() {
        Product product = new Product(PRODUCT_ID_5, PRODUCT_NAME_5, CATEGORY_5, PRICE_5);

        assertEquals(PRODUCT_ID_5, product.getId(), "Product ID should match");
        assertEquals(PRODUCT_NAME_5, product.getName(), "Complex product name should be preserved");
        assertTrue(product.getName().contains("65\""), "Product name should contain quote character");
        assertTrue(product.getName().contains(","), "Product name should contain comma");
        assertEquals(CATEGORY_5, product.getCategory(), "Category with pipes should be preserved");
        assertEquals(PRICE_5, product.getPrice(), 0.001, "Price should match");
    }

    /**
     * Test getters for simple product.
     */
    @Test
    void allGetters_simpleProduct() {
        Product product = new Product("TEST123", "Test Product", "Category", 10.00);

        assertEquals("TEST123", product.getId(), "getId() should return product ID");
        assertEquals("Test Product", product.getName(), "getName() should return product name");
        assertEquals("Category", product.getCategory(), "getCategory() should return category");
        assertEquals(10.00, product.getPrice(), 0.001, "getPrice() should return price");
    }

    /**
     * Test getters with single category (no pipes).
     */
    @Test
    void getCategory_singleCategory() {
        Product product = new Product("2001", "Simple Product", "Electronics", 49.99);

        assertEquals("Electronics", product.getCategory(), "Single category should be stored without pipes");
        assertFalse(product.getCategory().contains("|"), "Single category should not contain pipe character");
    }

    /**
     * Test getters with multiple pipe-separated categories.
     */
    @Test
    void getCategory_multipleCategoriesWithPipes() {
        Product product = new Product("3001", "Gaming Keyboard", "Electronics|Gaming|Accessories", 79.99);

        assertEquals("Electronics|Gaming|Accessories", product.getCategory(),
                "Multiple categories should be stored with pipe separators");
        assertTrue(product.getCategory().contains("|"), "Multiple categories should contain pipe separators");
    }

    /**
     * Test price with zero value.
     */
    @Test
    void getPrice_zeroPrice() {
        Product product = new Product("4001", "Free Item", "Category", 0.00);

        assertEquals(0.00, product.getPrice(), 0.001, "Zero price should be allowed");
    }

    /**
     * Test price with large value.
     */
    @Test
    void getPrice_largePrice() {
        Product product = new Product("5001", "Expensive Item", "Category", 99999.99);

        assertEquals(99999.99, product.getPrice(), 0.001, "Large prices should be supported");
    }

    /**
     * Test toString() formatting with README example.
     * Expected format:
     * Product ID: 1001
     * Name: Wireless Mouse
     * Category: Electronics|Accessories
     * Price: $29.99
     */
    @Test
    void toString_readmeExampleFormat() {
        Product product = new Product(PRODUCT_ID_1, PRODUCT_NAME_1, CATEGORY_1, PRICE_1);
        String result = product.toString();

        assertTrue(result.contains("Product ID: 1001"), "toString should contain product ID with label");
        assertTrue(result.contains("Name: Wireless Mouse"), "toString should contain product name with label");
        assertTrue(result.contains("Category: Electronics|Accessories"), "toString should contain category with label");
        assertTrue(result.contains("Price: $29.99"), "toString should contain price with $ symbol");
    }

    /**
     * Test toString() price formatting - should always show 2 decimal places.
     */
    @Test
    void toString_priceFormatting_twoDecimalPlaces() {
        Product product1 = new Product("1001", "Product", "Category", 10.5);
        Product product2 = new Product("1002", "Product", "Category", 10.00);

        assertTrue(product1.toString().contains("$10.50"),
                "Price 10.5 should be formatted as $10.50 with two decimal places");
        assertTrue(product2.toString().contains("$10.00"),
                "Price 10.00 should be formatted as $10.00 with two decimal places");
    }

    /**
     * Test toString() with product name containing quotes.
     */
    @Test
    void toString_productNameWithQuotes() {
        Product product = new Product(PRODUCT_ID_3, PRODUCT_NAME_3, CATEGORY_3, PRICE_3);
        String result = product.toString();

        assertTrue(result.contains("Book: \"The Art of War\""),
                "toString should preserve quotes in product name");
    }

    /**
     * Test equals() with same object reference.
     * Requirement: equals() should return true for same object.
     */
    @Test
    void equals_sameObjectReference() {
        Product product = new Product(PRODUCT_ID_1, PRODUCT_NAME_1, CATEGORY_1, PRICE_1);

        assertEquals(product, product, "Product should equal itself (same reference)");
    }

    /**
     * Test equals() with different products having same ID.
     * Requirement: Products are equal if they have the same product ID.
     */
    @Test
    void equals_sameProductId_differentOtherFields() {
        Product product1 = new Product("1001", "Product A", "Category1", 10.00);
        Product product2 = new Product("1001", "Product B", "Category2", 20.00);

        assertEquals(product1, product2,
                "Products with same ID should be equal (even if other fields differ)");
    }

    /**
     * Test equals() with different product IDs.
     * Products should NOT be equal if IDs differ.
     */
    @Test
    void equals_differentProductIds() {
        Product product1 = new Product("1001", "Product", "Category", 10.00);
        Product product2 = new Product("1002", "Product", "Category", 10.00);

        assertNotEquals(product1, product2,
                "Products with different IDs should NOT be equal");
    }

    /**
     * Test equals() with null reference.
     */
    @Test
    void equals_nullReference() {
        Product product = new Product(PRODUCT_ID_1, PRODUCT_NAME_1, CATEGORY_1, PRICE_1);

        assertNotEquals(product, null, "Product should NOT equal null");
        assertNotEquals(null, product, "null should NOT equal Product");
    }

    /**
     * Test equals() with different class type.
     */
    @Test
    void equals_differentClassType() {
        Product product = new Product(PRODUCT_ID_1, PRODUCT_NAME_1, CATEGORY_1, PRICE_1);
        String notAProduct = "Not a product";

        assertNotEquals(product, notAProduct,
                "Product should NOT equal object of different class");
    }

    /**
     * Test equals() with empty string fields.
     * Edge case: products with all empty fields should still be equal if IDs match.
     */
    @Test
    void equals_emptyStringFields() {
        Product product1 = new Product("", "", "", 0.00);
        Product product2 = new Product("", "", "", 0.00);

        assertEquals(product1, product2,
                "Products with same empty ID should be equal");
    }

    /**
     * Test product with alphanumeric ID.
     */
    @Test
    void constructor_alphanumericProductId() {
        Product product = new Product("ABC-123-XYZ", "Product", "Category", 50.00);

        assertEquals("ABC-123-XYZ", product.getId(),
                "Alphanumeric product IDs should be supported");
    }

    /**
     * Test product with very long name.
     */
    @Test
    void constructor_longProductName() {
        String longName = "This is a very long product name that contains many words and characters to test the system's ability to handle lengthy product descriptions";
        Product product = new Product("1001", longName, "Category", 10.00);

        assertEquals(longName, product.getName(),
                "Long product names should be stored completely");
    }

    /**
     * Test product with many categories (10 categories as per requirements).
     */
    @Test
    void constructor_manyCategoriesTenCategories() {
        String categories = "Cat1|Cat2|Cat3|Cat4|Cat5|Cat6|Cat7|Cat8|Cat9|Cat10";
        Product product = new Product("1001", "Product", categories, 10.00);

        assertEquals(categories, product.getCategory(),
                "Should support up to 10 categories as per requirements");
        assertEquals(9, product.getCategory().chars().filter(ch -> ch == '|').count(),
                "Should have 9 pipe separators for 10 categories");
    }
}