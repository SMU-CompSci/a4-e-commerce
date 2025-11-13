import com.student_work.Product;
import com.student_work.RedBlackTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhanced comprehensive unit tests for Left-Leaning Red-Black Tree (LLRB) implementation.
 * Tests insertion, search, and validation functions for e-commerce product management.
 *
 * LLRB Properties Tested:
 * - All red links lean left (no right-leaning red links)
 * - No node has two red children
 * - Perfect black balance (all paths have same number of black nodes)
 * - Root is always black
 * - Corresponds to 2-3 tree representation
 */
class RedBlackTreeTest {

    private RedBlackTree tree;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private PrintStream originalErr;

    @BeforeEach
    void setUp() {
        tree = new RedBlackTree();

        // Capture System.out and System.err for validation message testing
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        originalErr = System.err;
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        // Restore original streams
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    // ==================== HELPER METHODS FOR DIAGNOSTICS ====================

    /**
     * Enhanced assertion for tree validation with detailed diagnostics
     */
    private void assertTreeValid(RedBlackTree tree, String context) {
        boolean isValid = tree.validate();
        if (!isValid) {
            System.setOut(originalOut);
            System.setErr(originalErr);

            System.err.println("\n" + "=".repeat(70));
            System.err.println("❌ LLRB VALIDATION FAILED: " + context);
            System.err.println("=".repeat(70));

            // Print validation errors that were captured
            String capturedOutput = outputStream.toString();
            if (!capturedOutput.isEmpty()) {
                System.err.println("\nValidation Errors:");
                System.err.println(capturedOutput);
            }

            System.err.println("\nTree State:");
            tree.printTree();

            System.err.println("\nDiagnostic Information:");
            System.err.println("  Size: " + tree.size());
            System.err.println("  Height: " + tree.height());
            System.err.println("  Red Links: " + tree.countRedLinks());

            System.err.println("\n💡 DEBUGGING HINTS:");
            if (capturedOutput.contains("Right-leaning red link")) {
                System.err.println("  → Issue: Right-leaning red link detected");
                System.err.println("  → Cause: rotateLeft() may not be called when right child is red");
                System.err.println("  → Fix: Check that after inserting to the right, you call:");
                System.err.println("         if (isRed(h.right) && !isRed(h.left)) h = rotateLeft(h);");
            }
            if (capturedOutput.contains("Two consecutive red links")) {
                System.err.println("  → Issue: Two consecutive red nodes found");
                System.err.println("  → Cause: Parent and child both red (red-red violation)");
                System.err.println("  → Fix: Check that you rotate right when left child and left-left");
                System.err.println("         grandchild are both red, or flip colors when both children are red");
            }
            if (capturedOutput.contains("Black balance violated")) {
                System.err.println("  → Issue: Unequal black heights on different paths");
                System.err.println("  → Cause: Rotations or color flips not preserving black height");
                System.err.println("  → Fix: Verify that rotations preserve the color properly and");
                System.err.println("         flipColors is called when both children are red");
            }
            if (capturedOutput.contains("Root is not black")) {
                System.err.println("  → Issue: Root node is red instead of black");
                System.err.println("  → Cause: Missing root.color = BLACK after insertion");
                System.err.println("  → Fix: Ensure root.color = BLACK is set after insert() returns");
            }
            System.err.println("=".repeat(70) + "\n");
        }
        assertTrue(isValid, context + " - Tree should be valid LLRB (see diagnostics above)");
    }

    /**
     * Enhanced assertion for 2-3 tree property with diagnostics
     */
    private void assert23TreeValid(RedBlackTree tree, String context) {
        boolean is23 = tree.is23();
        if (!is23) {
            System.setOut(originalOut);
            System.setErr(originalErr);

            System.err.println("\n" + "=".repeat(70));
            System.err.println("❌ 2-3 TREE PROPERTY VIOLATED: " + context);
            System.err.println("=".repeat(70));

            System.err.println("\nTree State:");
            tree.printTree();

            System.err.println("\n💡 DEBUGGING HINTS:");
            System.err.println("  → Issue: Tree does not correspond to a valid 2-3 tree");
            System.err.println("  → Common causes:");
            System.err.println("    1. Right-leaning red links (should lean left)");
            System.err.println("    2. Consecutive red nodes (red parent with red child)");
            System.err.println("  → Fix: Review the three fix-up operations after recursive insert:");
            System.err.println("    1. Rotate left if right is red and left is black");
            System.err.println("    2. Rotate right if left and left.left are both red");
            System.err.println("    3. Flip colors if both children are red");
            System.err.println("=".repeat(70) + "\n");
        }
        assertTrue(is23, context + " - Tree should maintain 2-3 tree property (see diagnostics above)");
    }

    /**
     * Enhanced size assertion with actual vs expected
     */
    private void assertSizeEquals(int expected, RedBlackTree tree, String context) {
        int actual = tree.size();
        if (actual != expected) {
            System.setOut(originalOut);
            System.setErr(originalErr);

            System.err.println("\n❌ SIZE MISMATCH: " + context);
            System.err.println("  Expected: " + expected);
            System.err.println("  Actual:   " + actual);
            System.err.println("  Difference: " + (actual - expected));

            if (actual < expected) {
                System.err.println("\n💡 HINT: Size is smaller than expected");
                System.err.println("  → Possible causes:");
                System.err.println("    1. Size not incremented during insert");
                System.err.println("    2. Duplicate insertions not handled (size++ called even for updates)");
            } else {
                System.err.println("\n💡 HINT: Size is larger than expected");
                System.err.println("  → Possible cause: Size incremented even for duplicate insertions");
                System.err.println("  → Fix: Only increment size for new insertions, not updates");
            }
        }
        assertEquals(expected, actual, context);
    }

    /**
     * Enhanced height assertion with bounds checking
     */
    private void assertHeightInBounds(RedBlackTree tree, int maxExpected, String context) {
        int actual = tree.height();
        if (actual > maxExpected) {
            System.setOut(originalOut);
            System.setErr(originalErr);

            System.err.println("\n❌ HEIGHT EXCEEDS BOUNDS: " + context);
            System.err.println("  Expected: <= " + maxExpected);
            System.err.println("  Actual:   " + actual);
            System.err.println("  Excess:   " + (actual - maxExpected));

            System.err.println("\n💡 HINT: Tree is deeper than logarithmic bound");
            System.err.println("  → Possible causes:");
            System.err.println("    1. Rotations not being performed correctly");
            System.err.println("    2. Tree becoming unbalanced (check validation)");
            System.err.println("    3. Color flips not working properly");
            System.err.println("  → Expected LLRB height: h <= 2*log₂(n+1)");
            System.err.println("  → For comparison, a perfectly balanced tree would have height: " +
                    (int)Math.ceil(Math.log(tree.size() + 1) / Math.log(2)));
        }
        assertTrue(actual <= maxExpected,
                String.format("%s - Height %d should be <= %d", context, actual, maxExpected));
    }

    /**
     * Enhanced search assertion with detailed output
     */
    private void assertProductFound(int productId, RedBlackTree tree, String context) {
        Product found = tree.search(productId);
        if (found == null) {
            System.setOut(originalOut);
            System.setErr(originalErr);

            System.err.println("\n❌ PRODUCT NOT FOUND: " + context);
            System.err.println("  Looking for: " + productId);
            System.err.println("  Result: null");

            System.err.println("\nTree State:");
            tree.printTree();

            System.err.println("\n💡 HINT: Product not found in tree");
            System.err.println("  → Possible causes:");
            System.err.println("    1. Product was never inserted");
            System.err.println("    2. BST search logic has a bug (check comparisons)");
            System.err.println("    3. Tree structure was corrupted during rotations");
            System.err.println("  → Check: Does the product ID exist in the tree above?");
        }
        assertNotNull(found, context + " - Should find product " + productId);
    }

    /**
     * Enhanced search assertion for null (product should NOT be found)
     */
    private void assertProductNotFound(int productId, RedBlackTree tree, String context) {
        Product found = tree.search(productId);
        if (found != null) {
            System.setOut(originalOut);
            System.setErr(originalErr);

            System.err.println("\n❌ UNEXPECTED PRODUCT FOUND: " + context);
            System.err.println("  Looking for: " + productId);
            System.err.println("  Expected: null");
            System.err.println("  Found: " + found.getId() + " - " + found.getName());

            System.err.println("\n💡 HINT: Found a product that shouldn't exist");
            System.err.println("  → This product was likely inserted but shouldn't have been");
            System.err.println("  → Or test expectations are incorrect");
        }
        assertNull(found, context + " - Should NOT find product " + productId);
    }

    // ==================== CONSTRUCTOR TESTS ====================

    @Test
    @DisplayName("Constructor creates empty tree")
    void testConstructor_emptyTree() {
        RedBlackTree newTree = new RedBlackTree();

        assertSizeEquals(0, newTree, "New tree should have size 0");
        assertTrue(newTree.isEmpty(), "New tree should be empty");

        int actualHeight = newTree.height();
        if (actualHeight != 0) {
            System.setOut(originalOut);
            System.err.println("❌ Empty tree height mismatch - Expected: 0, Actual: " + actualHeight);
        }
        assertEquals(0, actualHeight, "Empty tree should have height 0");
    }

    @Test
    @DisplayName("Constructor creates valid LLRB tree")
    void testConstructor_validTree() {
        RedBlackTree newTree = new RedBlackTree();
        assertTreeValid(newTree, "New empty tree");
        assert23TreeValid(newTree, "New empty tree");
    }

    // ==================== SIZE AND EMPTY TESTS ====================

    @Test
    @DisplayName("Size increases with insertions")
    void testSize_increasesWithInsertions() {
        assertSizeEquals(0, tree, "Initial tree");

        tree.insert(new Product(1, "Product1", "Cat1", 10.0));
        assertSizeEquals(1, tree, "After first insert");

        tree.insert(new Product(2, "Product2", "Cat2", 20.0));
        assertSizeEquals(2, tree, "After second insert");

        tree.insert(new Product(3, "Product3", "Cat3", 30.0));
        assertSizeEquals(3, tree, "After third insert");
    }

    @Test
    @DisplayName("isEmpty returns correct values")
    void testIsEmpty() {
        boolean initialEmpty = tree.isEmpty();
        if (!initialEmpty) {
            System.setOut(originalOut);
            System.err.println("❌ New tree should be empty but isEmpty() returned false");
            System.err.println("  → Check: Is size initialized to 0 in constructor?");
        }
        assertTrue(initialEmpty, "New tree should be empty");

        tree.insert(new Product(1, "Product1", "Cat1", 10.0));
        boolean afterInsert = tree.isEmpty();
        if (afterInsert) {
            System.setOut(originalOut);
            System.err.println("❌ Tree with 1 element reports as empty");
            System.err.println("  → Check: Is size being incremented on insert?");
            System.err.println("  → Actual size: " + tree.size());
        }
        assertFalse(afterInsert, "Tree with 1 element should not be empty");
    }

    // ==================== HEIGHT TESTS ====================

    @Test
    @DisplayName("Height of empty tree is 0")
    void testHeight_emptyTree() {
        int actual = tree.height();
        if (actual != 0) {
            System.setOut(originalOut);
            System.err.println("❌ Empty tree height - Expected: 0, Actual: " + actual);
            System.err.println("  → Check: Does height() handle null root correctly?");
        }
        assertEquals(0, actual, "Empty tree should have height 0");
    }

    @Test
    @DisplayName("Height of single node tree is 1")
    void testHeight_singleNode() {
        tree.insert(new Product(1, "Product1", "Cat1", 10.0));
        int actual = tree.height();
        if (actual != 1) {
            System.setOut(originalOut);
            System.err.println("❌ Single node height - Expected: 1, Actual: " + actual);
            System.err.println("  → Check: Is height counting nodes or edges?");
            System.err.println("  → Should return 1 for single node (not 0)");
        }
        assertEquals(1, actual, "Single node tree should have height 1");
    }

    @Test
    @DisplayName("Height increases as tree grows")
    void testHeight_growingTree() {
        tree.insert(new Product(5, "Product5", "Cat5", 50.0));
        int height1 = tree.height();

        tree.insert(new Product(3, "Product3", "Cat3", 30.0));
        tree.insert(new Product(7, "Product7", "Cat7", 70.0));
        int height2 = tree.height();

        tree.insert(new Product(1, "Product1", "Cat1", 10.0));
        tree.insert(new Product(4, "Product4", "Cat4", 40.0));
        tree.insert(new Product(6, "Product6", "Cat6", 60.0));
        tree.insert(new Product(9, "Product9", "Cat9", 90.0));
        int height3 = tree.height();

        if (height2 < height1) {
            System.setOut(originalOut);
            System.err.println("❌ Height decreased: " + height1 + " → " + height2);
            System.err.println("  → This should never happen!");
        }
        assertTrue(height2 >= height1,
                String.format("Height should not decrease: %d → %d", height1, height2));

        if (height3 < height2) {
            System.setOut(originalOut);
            System.err.println("❌ Height decreased: " + height2 + " → " + height3);
        }
        assertTrue(height3 >= height2,
                String.format("Height should not decrease: %d → %d", height2, height3));
    }

    @Test
    @DisplayName("Height respects LLRB logarithmic bound: h <= 2*log2(n+1)")
    void testHeight_logarithmicBound() {
        // Insert 15 nodes
        for (int i = 1; i <= 15; i++) {
            tree.insert(new Product(i, "Product" + i, "Cat", 10.0 * i));
        }

        int height = tree.height();
        int maxHeight = (int) (2 * Math.log(16) / Math.log(2));

        assertHeightInBounds(tree, maxHeight, "15 node tree");
    }

    // ==================== INSERT TESTS ====================

    @Test
    @DisplayName("Insert into empty tree creates valid LLRB")
    void testInsert_emptyTree() {
        Product p = new Product(5, "Product5", "Cat5", 50.0);
        tree.insert(p);

        assertSizeEquals(1, tree, "After first insert");
        assertTreeValid(tree, "After first insert");
        assert23TreeValid(tree, "After first insert");
    }

    @Test
    @DisplayName("Insert maintains LLRB balance")
    void testInsert_maintainsBalance() {
        int[] ids = {5, 3, 7, 1, 4, 6, 9};

        for (int i = 0; i < ids.length; i++) {
            int id = ids[i];
            tree.insert(new Product(id, "Product", "Cat", 10.0));
            assertTreeValid(tree, "After inserting " + id + " (" + (i+1) + " of " + ids.length + ")");
            assert23TreeValid(tree, "After inserting " + id);
        }

        assertSizeEquals(7, tree, "After all insertions");
    }

    @Test
    @DisplayName("Insert sequential ascending order maintains LLRB properties")
    void testInsert_sequentialAscending() {
        for (int i = 1; i <= 10; i++) {
            tree.insert(new Product(i, "Product" + i, "Cat", 10.0 * i));
            assertTreeValid(tree, "Sequential ascending insert #" + i);
        }

        assertSizeEquals(10, tree, "After 10 sequential ascending insertions");
        assert23TreeValid(tree, "Sequential ascending final tree");
    }

    @Test
    @DisplayName("Insert sequential descending order maintains LLRB properties")
    void testInsert_sequentialDescending() {
        for (int i = 10; i >= 1; i--) {
            tree.insert(new Product(i, "Product" + i, "Cat", 10.0 * i));
            assertTreeValid(tree, "Sequential descending insert #" + (11 - i));
        }

        assertSizeEquals(10, tree, "After 10 sequential descending insertions");
        assert23TreeValid(tree, "Sequential descending final tree");
    }

    @Test
    @DisplayName("Insert random order maintains LLRB properties")
    void testInsert_randomOrder() {
        int[] randomIds = {8, 3, 10, 1, 6, 9, 2, 7, 4, 5};

        for (int i = 0; i < randomIds.length; i++) {
            tree.insert(new Product(randomIds[i], "Product", "Cat", 10.0));
            assertTreeValid(tree, "Random insert #" + (i+1) + " (" + randomIds[i] + ")");
        }

        assertSizeEquals(10, tree, "After 10 random insertions");
        assert23TreeValid(tree, "Random order final tree");
    }

    @Test
    @DisplayName("Update existing product maintains LLRB properties")
    void testInsert_updateExisting() {
        Product original = new Product(1, "Original", "Cat1", 10.0);
        tree.insert(original);
        assertSizeEquals(1, tree, "After initial insert");

        Product updated = new Product(1, "Updated", "Cat2", 20.0);
        tree.insert(updated);

        int actualSize = tree.size();
        if (actualSize != 1) {
            System.setOut(originalOut);
            System.err.println("\n❌ SIZE ERROR ON UPDATE:");
            System.err.println("  Expected: 1 (size should NOT increase on update)");
            System.err.println("  Actual: " + actualSize);
            System.err.println("\n💡 HINT: When updating an existing product:");
            System.err.println("  → Do NOT increment size");
            System.err.println("  → Only increment size for NEW insertions");
            System.err.println("  → Check your insert() method's duplicate handling");
        }
        assertSizeEquals(1, tree, "After update (size should NOT increase)");

        Product found = tree.search(1);
        assertProductFound(1, tree, "Updated product");

        String actualName = found.getName();
        if (!actualName.equals("Updated")) {
            System.setOut(originalOut);
            System.err.println("\n❌ PRODUCT NOT UPDATED:");
            System.err.println("  Expected name: 'Updated'");
            System.err.println("  Actual name: '" + actualName + "'");
            System.err.println("\n💡 HINT: Product data not being updated");
            System.err.println("  → Check: h.product = product in duplicate case");
        }
        assertEquals("Updated", actualName, "Product name should be updated");

        assertTreeValid(tree, "After product update");
    }

    // ==================== SEARCH TESTS ====================

    @Test
    @DisplayName("Search in empty tree returns null")
    void testSearch_emptyTree() {
        assertProductNotFound(1, tree, "Search in empty tree");
    }

    @Test
    @DisplayName("Search finds single inserted product")
    void testSearch_singleProduct() {
        Product p = new Product(5, "Product5", "Cat5", 50.0);
        tree.insert(p);

        assertProductFound(5, tree, "After inserting 5");
        assertProductNotFound(1, tree, "Non-existent product 1");
    }

    @Test
    @DisplayName("Search finds all inserted products")
    void testSearch_multipleProducts() {
        int[] ids = {5, 3, 7, 1, 9};

        for (int id : ids) {
            tree.insert(new Product(id, "Product", "Cat", 10.0));
        }

        for (int id : ids) {
            assertProductFound(id, tree, "Product " + id);
        }

        assertProductNotFound(999, tree, "Non-existent product 999");
    }

    @Test
    @DisplayName("Search handles ID variations correctly")
    void testSearch_idVariations() {
        tree.insert(new Product(1, "Product1", "Cat1", 10.0));

        assertProductFound(1, tree, "Exact match");
        assertProductNotFound(10, tree, "Different ID (should not match)");
        assertProductNotFound(100, tree, "Different ID format");
    }

    @Test
    @DisplayName("Search after many insertions")
    void testSearch_afterManyInsertions() {
        for (int i = 1; i <= 100; i++) {
            tree.insert(new Product(i, "Product" + i, "Cat", 10.0 * i));
        }

        // Test specific searches
        assertProductFound(1, tree, "First product");
        assertProductFound(50, tree, "Middle product");
        assertProductFound(100, tree, "Last product");
        assertProductNotFound(0, tree, "Before range");
        assertProductNotFound(101, tree, "After range");
    }

    // ==================== VALIDATION TESTS ====================

    @Test
    @DisplayName("Empty tree is valid LLRB")
    void testValidate_emptyTree() {
        assertTreeValid(tree, "Empty tree");
        assert23TreeValid(tree, "Empty tree");
    }

    @Test
    @DisplayName("Validation passes after each insertion")
    void testValidate_afterEachInsertion() {
        for (int i = 1; i <= 20; i++) {
            tree.insert(new Product(i, "Product" + i, "Cat", 10.0 * i));

            assertTreeValid(tree, "After insertion " + i);
            assert23TreeValid(tree, "After insertion " + i);
        }
    }

    @Test
    @DisplayName("Red links are always left-leaning")
    void testValidate_leftLeaningProperty() {
        // Insert pattern that might create right-leaning reds if not fixed
        int[] ids = {5, 7, 3, 9, 1};

        for (int id : ids) {
            tree.insert(new Product(id, "Product", "Cat", 10.0));
            assertTreeValid(tree, "Left-leaning check after inserting " + id);
        }
    }

    @Test
    @DisplayName("Root is always black after insertions")
    void testValidate_rootAlwaysBlack() {
        for (int i = 1; i <= 10; i++) {
            tree.insert(new Product(i, "Product" + i, "Cat", 10.0 * i));

            // The validate() method checks this, but we emphasize it
            assertTreeValid(tree, "Root black check after insertion " + i);
        }
    }

    // ==================== INTEGRATION TESTS ====================

    @Test
    @DisplayName("Integration test - Large tree with validation")
    void integrationTest_largeTree() {
        int numProducts = 100;

        // Insert products
        for (int i = 1; i <= numProducts; i++) {
            tree.insert(new Product(i, "Product" + i, "Cat", 10.0 * i));
        }

        // Validate size
        assertSizeEquals(numProducts, tree, "After inserting " + numProducts + " products");

        // Search all products
        for (int i = 1; i <= numProducts; i++) {
            assertProductFound(i, tree, "Product " + i);
        }

        // Validate LLRB properties
        assertTreeValid(tree, "Large tree with " + numProducts + " nodes");
        assert23TreeValid(tree, "Large tree with " + numProducts + " nodes");

        // Verify height is logarithmic
        int height = tree.height();
        int maxHeight = (int) (2 * Math.log(numProducts + 1) / Math.log(2)) + 2;
        assertHeightInBounds(tree, maxHeight, numProducts + " node tree");

        // Check red links are sparse
        int redLinks = tree.countRedLinks();
        int maxRedLinks = numProducts / 2;
        if (redLinks >= maxRedLinks) {
            System.setOut(originalOut);
            System.err.println("\n❌ TOO MANY RED LINKS:");
            System.err.println("  Red links: " + redLinks);
            System.err.println("  Max expected: < " + maxRedLinks);
            System.err.println("  Total nodes: " + numProducts);
            System.err.println("\n💡 HINT: Excessive red links suggest:");
            System.err.println("  → Color flips may not be working");
            System.err.println("  → Tree is not maintaining 2-3-4 tree property");
        }
        assertTrue(redLinks < maxRedLinks,
                String.format("Red links (%d) should be < %d", redLinks, maxRedLinks));
    }

    @Test
    @DisplayName("Integration test - All LLRB properties maintained")
    void integrationTest_allPropertiesMaintained() {
        for (int i = 1; i <= 50; i++) {
            tree.insert(new Product(i, "Product" + i, "Cat", 10.0 * i));

            // Comprehensive validation after each insert
            assertTreeValid(tree, "Comprehensive check at size " + i);
            assert23TreeValid(tree, "2-3 tree check at size " + i);
            assertSizeEquals(i, tree, "Size check at insertion " + i);

            // Height should be logarithmic
            int height = tree.height();
            int maxHeight = (int) (2 * Math.log(i + 1) / Math.log(2)) + 2;
            assertHeightInBounds(tree, maxHeight, "Height check at size " + i);
        }
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    @DisplayName("Edge case - Single node tree is valid LLRB")
    void edgeCase_singleNode() {
        tree.insert(new Product(1, "Product1", "Cat1", 10.0));

        assertSizeEquals(1, tree, "Single node");

        int height = tree.height();
        if (height != 1) {
            System.setOut(originalOut);
            System.err.println("❌ Single node height - Expected: 1, Actual: " + height);
        }
        assertEquals(1, height, "Single node height");

        assertProductFound(1, tree, "Single node");
        assertTreeValid(tree, "Single node tree");
        assert23TreeValid(tree, "Single node tree");
    }

    @Test
    @DisplayName("Edge case - Two node tree is valid LLRB")
    void edgeCase_twoNodes() {
        tree.insert(new Product(1, "Product1", "Cat1", 10.0));
        tree.insert(new Product(2, "Product2", "Cat2", 20.0));

        assertSizeEquals(2, tree, "Two nodes");
        assertProductFound(1, tree, "First of two nodes");
        assertProductFound(2, tree, "Second of two nodes");
        assertTreeValid(tree, "Two node tree");
        assert23TreeValid(tree, "Two node tree");
    }

    @Test
    @DisplayName("Edge case - All same category")
    void edgeCase_sameCategory() {
        for (int i = 1; i <= 10; i++) {
            tree.insert(new Product(i, "Product" + i, "SameCategory", 10.0 * i));
        }

        assertSizeEquals(10, tree, "Same category products");
        assertTreeValid(tree, "Same category tree");
        assert23TreeValid(tree, "Same category tree");
    }

    @Test
    @DisplayName("Edge case - Identical prices")
    void edgeCase_identicalPrices() {
        for (int i = 1; i <= 10; i++) {
            tree.insert(new Product(i, "Product" + i, "Cat" + i, 99.99));
        }

        assertSizeEquals(10, tree, "Identical prices");
        assertTreeValid(tree, "Identical prices tree");

        // Verify all have same price
        for (int i = 1; i <= 10; i++) {
            Product found = tree.search(i);
            assertNotNull(found, "Should find product " + i);

            double price = found.getPrice();
            if (Math.abs(price - 99.99) > 0.01) {
                System.setOut(originalOut);
                System.err.println("❌ Price mismatch for " + i);
                System.err.println("  Expected: 99.99");
                System.err.println("  Actual: " + price);
            }
            assertEquals(99.99, price, 0.01, "Price for " + i);
        }
    }

    @Test
    @DisplayName("Edge case - Very similar IDs")
    void edgeCase_similarIds() {
        tree.insert(new Product(1, "Product1", "Cat1", 10.0));
        tree.insert(new Product(11, "Product11", "Cat11", 110.0));
        tree.insert(new Product(101, "Product101", "Cat101", 1010.0));

        assertSizeEquals(3, tree, "Similar IDs");
        assertProductFound(1, tree, "ID 1");
        assertProductFound(11, tree, "ID 11");
        assertProductFound(101, tree, "ID 101");
        assertTreeValid(tree, "Similar IDs tree");
    }

    @Test
    @DisplayName("Edge case - E-commerce realistic product IDs")
    void edgeCase_realisticProductIds() {
        tree.insert(new Product(1001, "Wireless Mouse", "Electronics|Accessories", 29.99));
        tree.insert(new Product(1002, "Apple iPhone 14, 128GB", "Smartphones|Electronics", 999.99));
        tree.insert(new Product(1003, "Gaming Keyboard", "Electronics|Gaming", 79.99));
        tree.insert(new Product(1004, "USB-C Cable", "Accessories|Electronics", 12.99));
        tree.insert(new Product(1005, "Laptop Stand", "Office|Accessories", 34.99));

        assertSizeEquals(5, tree, "Realistic e-commerce products");

        assertProductFound(1001, tree, "Wireless mouse");
        assertProductFound(1002, tree, "iPhone");
        assertProductFound(1003, tree, "Gaming keyboard");
        assertProductFound(1004, tree, "USB cable");
        assertProductFound(1005, tree, "Laptop stand");

        assertTreeValid(tree, "Realistic products tree");
        assert23TreeValid(tree, "Realistic products tree");
    }

    // ==================== STRESS TESTS ====================

    @Test
    @DisplayName("Stress test - 1000 sequential inserts maintain LLRB")
    void stressTest_1000SequentialInserts() {
        System.setOut(originalOut);
        System.out.println("Running stress test with 1000 sequential inserts...");

        for (int i = 1; i <= 1000; i++) {
            tree.insert(new Product(i, "Product" + i, "Cat", 10.0 * i));

            // Only validate periodically to save time
            if (i % 100 == 0) {
                System.out.println("  Inserted " + i + " products, validating...");
                assertTreeValid(tree, "After " + i + " sequential inserts");
            }
        }

        assertSizeEquals(1000, tree, "1000 sequential inserts");
        assertTreeValid(tree, "Final tree with 1000 nodes");
        assert23TreeValid(tree, "Final tree with 1000 nodes");

        // Spot check searches
        assertProductFound(1, tree, "First product");
        assertProductFound(500, tree, "Middle product");
        assertProductFound(1000, tree, "Last product");

        // Verify logarithmic height
        int height = tree.height();
        int maxHeight = (int) (2 * Math.log(1001) / Math.log(2)) + 2;
        assertHeightInBounds(tree, maxHeight, "1000 node tree");

        System.out.println("✓ Stress test completed successfully!");
    }

    @Test
    @DisplayName("Stress test - Validates correctness of LLRB at various sizes")
    void stressTest_validationAtVariousSizes() {
        System.setOut(originalOut);
        System.out.println("Running validation at various tree sizes...");

        int[] testSizes = {10, 25, 50, 100, 250, 500};

        for (int size : testSizes) {
            System.out.println("  Testing size " + size + "...");
            RedBlackTree testTree = new RedBlackTree();

            // Insert size products
            for (int i = 1; i <= size; i++) {
                testTree.insert(new Product(i, "Product" + i, "Cat", 10.0 * i));
            }

            // Validate
            assertTreeValid(testTree, "Tree at size " + size);
            assert23TreeValid(testTree, "Tree at size " + size);
            assertSizeEquals(size, testTree, "Tree at size " + size);

            // Check height bound
            int height = testTree.height();
            int maxHeight = (int) (2 * Math.log(size + 1) / Math.log(2)) + 2;
            assertHeightInBounds(testTree, maxHeight, size + " node tree");
        }

        System.out.println("✓ All size validations passed!");
    }
}