import com.student_work.CsvParser;
import com.student_work.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CsvParser
 * Tests parsing of amazon-product-data.csv format with quoted fields,
 * escaped quotes, and pipe-separated categories
 */
class CsvParserTest {

    @TempDir
    Path tempDir;

    private Path testCsvFile;

    @BeforeEach
    void setUp() throws IOException {
        testCsvFile = tempDir.resolve("test_products.csv");
    }

    // ========== parseCsv Tests ==========

    @Test
    void parseCsv_simpleProducts_success() throws IOException {
        // Create CSV with simple unquoted products
        Files.write(testCsvFile, Arrays.asList(
                "P001,Basic Widget,Electronics,29.99",
                "P002,Simple Gadget,Home,15.50",
                "P003,Plain Item,Office,8.25"
        ));

        List<Product> products = CsvParser.parseCsv(testCsvFile.toString());

        assertEquals(3, products.size(), "Should parse 3 products");
        assertEquals("P001", products.get(0).getId());
        assertEquals("Basic Widget", products.get(0).getName());
        assertEquals("Electronics", products.get(0).getCategory());
        assertEquals(29.99, products.get(0).getPrice(), 0.001);
    }

    @Test
    void parseCsv_quotedProductNames_success() throws IOException {
        // Create CSV with quoted product names containing commas
        Files.write(testCsvFile, Arrays.asList(
                "P001,\"Widget, Deluxe Edition\",Electronics,49.99",
                "P002,\"Gadget, Pro Version\",Home,89.50"
        ));

        List<Product> products = CsvParser.parseCsv(testCsvFile.toString());

        assertEquals(2, products.size());
        assertEquals("Widget, Deluxe Edition", products.get(0).getName());
        assertEquals("Gadget, Pro Version", products.get(1).getName());
    }

    @Test
    void parseCsv_escapedQuotesInNames_success() throws IOException {
        // Create CSV with escaped quotes ("") in product names
        Files.write(testCsvFile, Arrays.asList(
                "P001,\"The \"\"Best\"\" Widget\",Electronics,39.99",
                "P002,\"Super \"\"Pro\"\" Gadget\",Home,59.99"
        ));

        List<Product> products = CsvParser.parseCsv(testCsvFile.toString());

        assertEquals(2, products.size());
        assertEquals("The \"Best\" Widget", products.get(0).getName());
        assertEquals("Super \"Pro\" Gadget", products.get(1).getName());
    }

    @Test
    void parseCsv_multipleCategoriesWithPipes_success() throws IOException {
        // Create CSV with pipe-separated categories
        Files.write(testCsvFile, Arrays.asList(
                "P001,Multi-Tool,\"Tools|Hardware|Outdoor\",45.99",
                "P002,Smart Device,\"Electronics|Home|Tech\",129.99"
        ));

        List<Product> products = CsvParser.parseCsv(testCsvFile.toString());

        assertEquals(2, products.size());
        assertEquals("Tools|Hardware|Outdoor", products.get(0).getCategory());
        assertEquals("Electronics|Home|Tech", products.get(1).getCategory());
    }

    @Test
    void parseCsv_emptyFile_returnsEmptyList() throws IOException {
        // Create empty CSV file
        Files.write(testCsvFile, Arrays.asList());

        List<Product> products = CsvParser.parseCsv(testCsvFile.toString());

        assertEquals(0, products.size(), "Empty file should return empty list");
    }

    @Test
    void parseCsv_fileWithEmptyLines_skipsEmptyLines() throws IOException {
        // Create CSV with empty lines
        Files.write(testCsvFile, Arrays.asList(
                "P001,Widget,Electronics,29.99",
                "",
                "P002,Gadget,Home,15.50",
                "   ",
                "P003,Item,Office,8.25"
        ));

        List<Product> products = CsvParser.parseCsv(testCsvFile.toString());

        assertEquals(3, products.size(), "Should skip empty lines and parse 3 products");
    }

    @Test
    void parseCsv_malformedLine_continuesParsing() throws IOException {
        // Create CSV with one malformed line
        Files.write(testCsvFile, Arrays.asList(
                "P001,Widget,Electronics,29.99",
                "P002,BadLine,TooFewFields",  // Only 3 fields - will cause error
                "P003,Gadget,Home,15.50"
        ));

        List<Product> products = CsvParser.parseCsv(testCsvFile.toString());

        // Should skip malformed line but parse others
        assertEquals(2, products.size(), "Should parse valid lines despite malformed line");
        assertEquals("P001", products.get(0).getId());
        assertEquals("P003", products.get(1).getId());
    }

    @Test
    void parseCsv_nonExistentFile_throwsIOException() {
        Path nonExistent = tempDir.resolve("does_not_exist.csv");

        assertThrows(IOException.class, () -> {
            CsvParser.parseCsv(nonExistent.toString());
        }, "Should throw IOException for non-existent file");
    }

    // ========== parseLine Tests ==========

    @Test
    void parseLine_simpleUnquotedLine_success() {
        String line = "P001,Basic Widget,Electronics,29.99";

        Product product = CsvParser.parseLine(line);

        assertEquals("P001", product.getId());
        assertEquals("Basic Widget", product.getName());
        assertEquals("Electronics", product.getCategory());
        assertEquals(29.99, product.getPrice(), 0.001);
    }

    @Test
    void parseLine_quotedFieldWithComma_success() {
        String line = "P001,\"Widget, Deluxe\",Electronics,49.99";

        Product product = CsvParser.parseLine(line);

        assertEquals("Widget, Deluxe", product.getName());
    }

    @Test
    void parseLine_escapedQuotes_success() {
        String line = "P001,\"The \"\"Best\"\" Widget\",Electronics,39.99";

        Product product = CsvParser.parseLine(line);

        assertEquals("The \"Best\" Widget", product.getName());
    }

    @Test
    void parseLine_multipleCategoriesWithPipes_success() {
        String line = "P001,Multi-Tool,\"Tools|Hardware|Outdoor\",45.99";

        Product product = CsvParser.parseLine(line);

        assertEquals("Tools|Hardware|Outdoor", product.getCategory());
    }

    @Test
    void parseLine_allFieldsQuoted_success() {
        String line = "\"P001\",\"Widget\",\"Electronics\",\"29.99\"";

        Product product = CsvParser.parseLine(line);

        assertEquals("P001", product.getId());
        assertEquals("Widget", product.getName());
        assertEquals("Electronics", product.getCategory());
        assertEquals(29.99, product.getPrice(), 0.001);
    }

    @Test
    void parseLine_tooFewFields_throwsException() {
        String line = "P001,Widget,Electronics";  // Missing price

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvParser.parseLine(line);
        });

        assertTrue(exception.getMessage().contains("Expected 4 fields"));
    }

    @Test
    void parseLine_tooManyFields_throwsException() {
        String line = "P001,Widget,Electronics,29.99,ExtraField";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvParser.parseLine(line);
        });

        assertTrue(exception.getMessage().contains("Expected 4 fields"));
    }

    @Test
    void parseLine_complexQuotedFields_success() {
        String line = "P001,\"Product with, many, commas\",\"Category|Sub|Sub2\",99.99";

        Product product = CsvParser.parseLine(line);

        assertEquals("Product with, many, commas", product.getName());
        assertEquals("Category|Sub|Sub2", product.getCategory());
    }

    // ========== parseProductId Tests ==========

    @Test
    void parseProductId_simpleId_success() {
        int result = CsvParser.parseProductId("001");
        assertEquals(1, result);
    }

    @Test
    void parseProductId_withWhitespace_trimmed() {
        int result = CsvParser.parseProductId("  001  ");
        assertEquals(1, result);
    }

    @Test
    void parseProductId_quoted_removesQuotes() {
        int result = CsvParser.parseProductId("\"P001\"");
        assertEquals(1, result);
    }

    @Test
    void parseProductId_alphanumeric_success() {
        int result = CsvParser.parseProductId("ABC123XYZ");
        assertEquals(123, result);
    }

    @Test
    void parseProductId_empty_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvParser.parseProductId("");
        });

        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    @Test
    void parseProductId_whitespaceOnly_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvParser.parseProductId("   ");
        });

        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    // ========== parseProductName Tests ==========

    @Test
    void parseProductName_simple_success() {
        String result = CsvParser.parseProductName("Basic Widget");
        assertEquals("Basic Widget", result);
    }

    @Test
    void parseProductName_quoted_removesQuotes() {
        String result = CsvParser.parseProductName("\"Deluxe Widget\"");
        assertEquals("Deluxe Widget", result);
    }

    @Test
    void parseProductName_quotedWithComma_removesQuotes() {
        String result = CsvParser.parseProductName("\"Widget, Deluxe Edition\"");
        assertEquals("Widget, Deluxe Edition", result);
    }

    @Test
    void parseProductName_escapedQuotes_unescapes() {
        String result = CsvParser.parseProductName("\"The \"\"Best\"\" Widget\"");
        assertEquals("The \"Best\" Widget", result);
    }

    @Test
    void parseProductName_multipleEscapedQuotes_unescapes() {
        String result = CsvParser.parseProductName("\"The \"\"Super\"\" \"\"Duper\"\" Widget\"");
        assertEquals("The \"Super\" \"Duper\" Widget", result);
    }

    @Test
    void parseProductName_withWhitespace_trimmed() {
        String result = CsvParser.parseProductName("  Basic Widget  ");
        assertEquals("Basic Widget", result);
    }

    @Test
    void parseProductName_empty_returnsEmpty() {
        String result = CsvParser.parseProductName("");
        assertEquals("", result);
    }

    // ========== parseCategory Tests ==========

    @Test
    void parseCategory_singleCategory_success() {
        String result = CsvParser.parseCategory("Electronics");
        assertEquals("Electronics", result);
    }

    @Test
    void parseCategory_multipleCategoriesWithPipes_success() {
        String result = CsvParser.parseCategory("Electronics|Home|Tech");
        assertEquals("Electronics|Home|Tech", result);
    }

    @Test
    void parseCategory_quoted_removesQuotes() {
        String result = CsvParser.parseCategory("\"Electronics\"");
        assertEquals("Electronics", result);
    }

    @Test
    void parseCategory_quotedWithPipes_removesQuotesKeepsPipes() {
        String result = CsvParser.parseCategory("\"Electronics|Home|Tech\"");
        assertEquals("Electronics|Home|Tech", result);
    }

    @Test
    void parseCategory_quotedWithCommaAndPipes_success() {
        String result = CsvParser.parseCategory("\"Home, Garden|Outdoor|Tools\"");
        assertEquals("Home, Garden|Outdoor|Tools", result);
    }

    @Test
    void parseCategory_escapedQuotesInCategory_unescapes() {
        String result = CsvParser.parseCategory("\"Category \"\"Special\"\" Name\"");
        assertEquals("Category \"Special\" Name", result);
    }

    @Test
    void parseCategory_withWhitespace_trimmed() {
        String result = CsvParser.parseCategory("  Electronics  ");
        assertEquals("Electronics", result);
    }

    // ========== parsePrice Tests ==========

    @Test
    void parsePrice_integer_success() {
        double result = CsvParser.parsePrice("50");
        assertEquals(50.0, result, 0.001);
    }

    @Test
    void parsePrice_decimal_success() {
        double result = CsvParser.parsePrice("29.99");
        assertEquals(29.99, result, 0.001);
    }

    @Test
    void parsePrice_zero_success() {
        double result = CsvParser.parsePrice("0.00");
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void parsePrice_withWhitespace_trimmed() {
        double result = CsvParser.parsePrice("  49.99  ");
        assertEquals(49.99, result, 0.001);
    }

    @Test
    void parsePrice_quoted_removesQuotesAndParses() {
        double result = CsvParser.parsePrice("\"39.99\"");
        assertEquals(39.99, result, 0.001);
    }

    @Test
    void parsePrice_largeNumber_success() {
        double result = CsvParser.parsePrice("9999.99");
        assertEquals(9999.99, result, 0.001);
    }

    @Test
    void parsePrice_manyDecimals_success() {
        double result = CsvParser.parsePrice("19.999");
        assertEquals(19.999, result, 0.001);
    }

    @Test
    void parsePrice_negative_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvParser.parsePrice("-10.00");
        });

        assertTrue(exception.getMessage().contains("cannot be negative"));
    }

    @Test
    void parsePrice_invalidFormat_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvParser.parsePrice("invalid");
        });

        assertTrue(exception.getMessage().contains("Invalid price format"));
    }

    @Test
    void parsePrice_empty_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            CsvParser.parsePrice("");
        });
    }

    @Test
    void parsePrice_withLetters_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CsvParser.parsePrice("29.99abc");
        });

        assertTrue(exception.getMessage().contains("Invalid price format"));
    }

    // ========== Integration Tests ==========

    @Test
    void integrationTest_realWorldCsvExample_success() throws IOException {
        // Create a realistic CSV file with various edge cases
        Files.write(testCsvFile, Arrays.asList(
                "B001,\"Widget, Standard Edition\",Electronics,29.99",
                "B002,Super Gadget,\"Home|Kitchen|Appliances\",149.50",
                "B003,\"The \"\"Ultimate\"\" Tool\",\"Tools|Hardware\",89.99",
                "B004,Simple Item,Office,12.50",
                "B005,\"Complex Product, with \"\"Quotes\"\" and Commas\",\"Category1|Category2|Category3\",199.99"
        ));

        List<Product> products = CsvParser.parseCsv(testCsvFile.toString());

        assertEquals(5, products.size());

        // Verify first product (quoted name with comma)
        assertEquals("B001", products.get(0).getId());
        assertEquals("Widget, Standard Edition", products.get(0).getName());
        assertEquals("Electronics", products.get(0).getCategory());
        assertEquals(29.99, products.get(0).getPrice(), 0.001);

        // Verify second product (multiple categories)
        assertEquals("B002", products.get(1).getId());
        assertEquals("Home|Kitchen|Appliances", products.get(1).getCategory());

        // Verify third product (escaped quotes in name)
        assertEquals("B003", products.get(2).getId());
        assertEquals("The \"Ultimate\" Tool", products.get(2).getName());

        // Verify fifth product (complex combination)
        assertEquals("B005", products.get(4).getId());
        assertEquals("Complex Product, with \"Quotes\" and Commas", products.get(4).getName());
        assertEquals("Category1|Category2|Category3", products.get(4).getCategory());
        assertEquals(199.99, products.get(4).getPrice(), 0.001);
    }

    @Test
    void integrationTest_edgeCasesInSingleFile_success() throws IOException {
        // Test various edge cases in one file
        Files.write(testCsvFile, Arrays.asList(
                "ID1,Name1,Cat1,10.00",                                    // Simple
                "ID2,\"Name, with comma\",Cat2,20.00",                    // Quoted name
                "ID3,Name3,\"Cat|SubCat\",30.00",                         // Multiple categories
                "ID4,\"Name \"\"quoted\"\" word\",Cat4,40.00",            // Escaped quotes
                "ID5,\"Name, with \"\"quotes\"\" and comma\",\"C1|C2\",50.00"  // Complex
        ));

        List<Product> products = CsvParser.parseCsv(testCsvFile.toString());

        assertEquals(5, products.size());
        assertEquals("Name, with comma", products.get(1).getName());
        assertEquals("Cat|SubCat", products.get(2).getCategory());
        assertEquals("Name \"quoted\" word", products.get(3).getName());
        assertEquals("Name, with \"quotes\" and comma", products.get(4).getName());
        assertEquals("C1|C2", products.get(4).getCategory());
    }

    @Test
    void integrationTest_mixedValidAndInvalidLines_parsesValidOnes() throws IOException {
        // Mix of valid and invalid lines
        Files.write(testCsvFile, Arrays.asList(
                "P001,Widget,Electronics,29.99",        // Valid
                "P002,Gadget",                          // Invalid - too few fields
                "P003,Tool,Hardware,49.99",             // Valid
                "P004,Item,Office,-5.00",               // Invalid - negative price
                "P005,Device,Tech,19.99"                // Valid
        ));

        List<Product> products = CsvParser.parseCsv(testCsvFile.toString());

        // Should parse only valid lines (P001, P003, P005)
        assertEquals(3, products.size());
        assertEquals("P001", products.get(0).getId());
        assertEquals("P003", products.get(1).getId());
        assertEquals("P005", products.get(2).getId());
    }
}