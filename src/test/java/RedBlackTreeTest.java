import com.student_work.Product;
import com.student_work.RedBlackTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for Red-Black Tree implementation.
 * Tests all functions including insertion, search, validation, rotations,
 * and family relationship functions.
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

    // ==================== CONSTRUCTOR TESTS ====================

    @Test
    @DisplayName("Constructor creates empty tree")
    void testConstructor_emptyTree() {
        RedBlackTree newTree = new RedBlackTree();
        assertEquals(0, newTree.size(), "New tree should have size 0");
        assertTrue(newTree.isEmpty(), "New tree should be empty");
        assertEquals(0, newTree.height(), "Empty tree should have height 0");
    }

    @Test
    @DisplayName("Constructor creates valid tree")
    void testConstructor_validTree() {
        RedBlackTree newTree = new RedBlackTree();
        assertTrue(newTree.validate(), "New tree should be valid");
        assertTrue(newTree.validateRootAndLeaves(), "New tree should pass root validation");
        assertTrue(newTree.validateNodeColors(), "New tree should pass color validation");
        assertTrue(newTree.validateRedNodeChildren(), "New tree should pass red-node validation");
        assertTrue(newTree.validateBlackHeight(), "New tree should pass black-height validation");
    }

    // ==================== SIZE AND EMPTY TESTS ====================

    @Test
    @DisplayName("Size increases with insertions")
    void testSize_increasesWithInsertions() {
        assertEquals(0, tree.size(), "Initial size should be 0");

        tree.insert(new Product("P001", "Product1", "Cat1", 10.0));
        assertEquals(1, tree.size(), "Size should be 1 after first insert");

        tree.insert(new Product("P002", "Product2", "Cat2", 20.0));
        assertEquals(2, tree.size(), "Size should be 2 after second insert");

        tree.insert(new Product("P003", "Product3", "Cat3", 30.0));
        assertEquals(3, tree.size(), "Size should be 3 after third insert");
    }

    @Test
    @DisplayName("Size doesn't increase on duplicate insertion")
    void testSize_duplicateInsertion() {
        tree.insert(new Product("P001", "Product1", "Cat1", 10.0));
        assertEquals(1, tree.size(), "Size should be 1");

        tree.insert(new Product("P001", "Product1Updated", "Cat1", 15.0));
        assertEquals(1, tree.size(), "Size should still be 1 after duplicate");
    }

    @Test
    @DisplayName("isEmpty returns correct values")
    void testIsEmpty() {
        assertTrue(tree.isEmpty(), "New tree should be empty");

        tree.insert(new Product("P001", "Product1", "Cat1", 10.0));
        assertFalse(tree.isEmpty(), "Tree with 1 element should not be empty");
    }

    // ==================== HEIGHT TESTS ====================

    @Test
    @DisplayName("Height of empty tree is 0")
    void testHeight_emptyTree() {
        assertEquals(0, tree.height(), "Empty tree should have height 0");
    }

    @Test
    @DisplayName("Height of single node tree is 1")
    void testHeight_singleNode() {
        tree.insert(new Product("P001", "Product1", "Cat1", 10.0));
        assertEquals(1, tree.height(), "Single node tree should have height 1");
    }

    @Test
    @DisplayName("Height increases as tree grows")
    void testHeight_growingTree() {
        tree.insert(new Product("P005", "Product5", "Cat5", 50.0));
        int height1 = tree.height();

        tree.insert(new Product("P003", "Product3", "Cat3", 30.0));
        tree.insert(new Product("P007", "Product7", "Cat7", 70.0));
        int height2 = tree.height();

        tree.insert(new Product("P001", "Product1", "Cat1", 10.0));
        tree.insert(new Product("P004", "Product4", "Cat4", 40.0));
        tree.insert(new Product("P006", "Product6", "Cat6", 60.0));
        tree.insert(new Product("P009", "Product9", "Cat9", 90.0));
        int height3 = tree.height();

        assertTrue(height2 >= height1, "Height should not decrease");
        assertTrue(height3 >= height2, "Height should not decrease");
    }

    @Test
    @DisplayName("Height respects log(n) bound")
    void testHeight_logarithmicBound() {
        // Insert 15 nodes
        for (int i = 1; i <= 15; i++) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
        }

        int height = tree.height();
        // For Red-Black tree: height <= 2 * log2(n+1)
        // For n=15: height <= 2 * log2(16) = 2 * 4 = 8
        assertTrue(height <= 8, "Height should be <= 8 for 15 nodes");
    }

    // ==================== INSERT TESTS ====================

    @Test
    @DisplayName("Insert into empty tree - Case 1")
    void testInsert_emptyTree_case1() {
        Product p = new Product("P005", "Product5", "Cat5", 50.0);
        tree.insert(p);

        assertEquals(1, tree.size(), "Size should be 1");
        assertTrue(tree.validate(), "Tree should be valid after first insert");
        assertTrue(tree.validateRootAndLeaves(), "Root should be black");
    }

    @Test
    @DisplayName("Insert creates balanced tree")
    void testInsert_maintainsBalance() {
        String[] ids = {"P005", "P003", "P007", "P001", "P004", "P006", "P009"};

        for (String id : ids) {
            tree.insert(new Product(id, "Product", "Cat", 10.0));
            assertTrue(tree.validate(), "Tree should be valid after inserting " + id);
        }

        assertEquals(7, tree.size(), "Size should be 7");
    }

    @Test
    @DisplayName("Insert handles duplicates by updating")
    void testInsert_duplicateUpdates() {
        tree.insert(new Product("P001", "OriginalName", "Cat1", 10.0));
        Product found1 = tree.search("P001");
        assertEquals("OriginalName", found1.getName(), "Should find original name");

        tree.insert(new Product("P001", "UpdatedName", "Cat2", 15.0));
        Product found2 = tree.search("P001");
        assertEquals("UpdatedName", found2.getName(), "Should find updated name");
        assertEquals(15.0, found2.getPrice(), 0.01, "Should find updated price");
        assertEquals(1, tree.size(), "Size should still be 1");
    }

    @Test
    @DisplayName("Insert sequential ascending order")
    void testInsert_sequentialAscending() {
        for (int i = 1; i <= 10; i++) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
        }

        assertEquals(10, tree.size(), "Size should be 10");
        assertTrue(tree.validate(), "Tree should remain balanced with ascending inserts");
    }

    @Test
    @DisplayName("Insert sequential descending order")
    void testInsert_sequentialDescending() {
        for (int i = 10; i >= 1; i--) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
        }

        assertEquals(10, tree.size(), "Size should be 10");
        assertTrue(tree.validate(), "Tree should remain balanced with descending inserts");
    }

    @Test
    @DisplayName("Insert random order maintains properties")
    void testInsert_randomOrder() {
        String[] ids = {"P008", "P003", "P010", "P001", "P006", "P004", "P007", "P002", "P009", "P005"};

        for (String id : ids) {
            tree.insert(new Product(id, "Product", "Cat", 10.0));
            assertTrue(tree.validate(), "Tree should be valid after each insert");
        }

        assertEquals(10, tree.size(), "Size should be 10");
    }

    @Test
    @DisplayName("Insert large number of nodes")
    void testInsert_largeScale() {
        int numNodes = 100;

        for (int i = 1; i <= numNodes; i++) {
            String id = String.format("P%04d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
        }

        assertEquals(numNodes, tree.size(), "Size should be " + numNodes);
        assertTrue(tree.validate(), "Tree should be valid with 100 nodes");

        // Verify height is logarithmic
        int height = tree.height();
        int maxHeight = (int) (2 * Math.log(numNodes + 1) / Math.log(2)) + 1;
        assertTrue(height <= maxHeight,
                String.format("Height %d should be <= %d for %d nodes", height, maxHeight, numNodes));
    }

    // ==================== SEARCH TESTS ====================

    @Test
    @DisplayName("Search in empty tree returns null")
    void testSearch_emptyTree() {
        Product result = tree.search("P001");
        assertNull(result, "Search in empty tree should return null");
    }

    @Test
    @DisplayName("Search finds single node")
    void testSearch_singleNode() {
        Product p = new Product("P001", "TestProduct", "TestCat", 99.99);
        tree.insert(p);

        Product found = tree.search("P001");
        assertNotNull(found, "Should find the product");
        assertEquals("P001", found.getId(), "Should find correct ID");
        assertEquals("TestProduct", found.getName(), "Should find correct name");
        assertEquals(99.99, found.getPrice(), 0.01, "Should find correct price");
    }

    @Test
    @DisplayName("Search finds all inserted nodes")
    void testSearch_multipleNodes() {
        String[] ids = {"P005", "P003", "P007", "P001", "P009"};

        for (String id : ids) {
            tree.insert(new Product(id, "Product" + id, "Cat", 10.0));
        }

        for (String id : ids) {
            Product found = tree.search(id);
            assertNotNull(found, "Should find product " + id);
            assertEquals(id, found.getId(), "Should find correct product ID");
        }
    }

    @Test
    @DisplayName("Search returns null for non-existent node")
    void testSearch_nonExistent() {
        tree.insert(new Product("P001", "Product1", "Cat1", 10.0));
        tree.insert(new Product("P003", "Product3", "Cat3", 30.0));
        tree.insert(new Product("P005", "Product5", "Cat5", 50.0));

        Product result = tree.search("P002");
        assertNull(result, "Should return null for non-existent product");

        result = tree.search("P000");
        assertNull(result, "Should return null for ID less than all nodes");

        result = tree.search("P999");
        assertNull(result, "Should return null for ID greater than all nodes");
    }

    @Test
    @DisplayName("Search after updates finds new values")
    void testSearch_afterUpdate() {
        tree.insert(new Product("P001", "OriginalName", "Cat1", 10.0));
        tree.insert(new Product("P001", "UpdatedName", "Cat2", 20.0));

        Product found = tree.search("P001");
        assertEquals("UpdatedName", found.getName(), "Should find updated name");
        assertEquals(20.0, found.getPrice(), 0.01, "Should find updated price");
    }

    // ==================== VALIDATION TESTS ====================

    @Test
    @DisplayName("Validate returns true for empty tree")
    void testValidate_emptyTree() {
        assertTrue(tree.validate(), "Empty tree should be valid");
    }

    @Test
    @DisplayName("Validate returns true after single insert")
    void testValidate_singleNode() {
        tree.insert(new Product("P001", "Product1", "Cat1", 10.0));
        assertTrue(tree.validate(), "Tree with single node should be valid");
    }

    @Test
    @DisplayName("Validate returns true for balanced tree")
    void testValidate_balancedTree() {
        String[] ids = {"P005", "P003", "P007", "P001", "P004", "P006", "P009"};

        for (String id : ids) {
            tree.insert(new Product(id, "Product", "Cat", 10.0));
        }

        assertTrue(tree.validate(), "Balanced tree should be valid");
    }

    @Test
    @DisplayName("Validate checks all properties")
    void testValidate_checksAllProperties() {
        for (int i = 1; i <= 20; i++) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));

            boolean valid = tree.validate();
            assertTrue(valid, "Tree should be valid after inserting " + i + " nodes");

            if (valid) {
                assertTrue(tree.validateRootAndLeaves(), "Root/leaves should be valid");
                assertTrue(tree.validateNodeColors(), "Node colors should be valid");
                assertTrue(tree.validateRedNodeChildren(), "Red node children should be valid");
                assertTrue(tree.validateBlackHeight(), "Black heights should be valid");
            }
        }
    }

    // ==================== VALIDATE ROOT AND LEAVES TESTS ====================

    @Test
    @DisplayName("ValidateRootAndLeaves passes for empty tree")
    void testValidateRootAndLeaves_emptyTree() {
        assertTrue(tree.validateRootAndLeaves(), "Empty tree should pass root validation");
    }

    @Test
    @DisplayName("ValidateRootAndLeaves passes after insertions")
    void testValidateRootAndLeaves_afterInsertions() {
        for (int i = 1; i <= 10; i++) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
            assertTrue(tree.validateRootAndLeaves(),
                    "Root should be black after inserting " + i + " nodes");
        }
    }

    // ==================== VALIDATE NODE COLORS TESTS ====================

    @Test
    @DisplayName("ValidateNodeColors passes for empty tree")
    void testValidateNodeColors_emptyTree() {
        assertTrue(tree.validateNodeColors(), "Empty tree should pass color validation");
    }

    @Test
    @DisplayName("ValidateNodeColors passes after insertions")
    void testValidateNodeColors_afterInsertions() {
        for (int i = 1; i <= 15; i++) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
            assertTrue(tree.validateNodeColors(),
                    "All nodes should have valid colors after inserting " + i + " nodes");
        }
    }

    // ==================== VALIDATE RED NODE CHILDREN TESTS ====================

    @Test
    @DisplayName("ValidateRedNodeChildren passes for empty tree")
    void testValidateRedNodeChildren_emptyTree() {
        assertTrue(tree.validateRedNodeChildren(),
                "Empty tree should pass red-node validation");
    }

    @Test
    @DisplayName("ValidateRedNodeChildren passes after insertions")
    void testValidateRedNodeChildren_afterInsertions() {
        for (int i = 1; i <= 15; i++) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
            assertTrue(tree.validateRedNodeChildren(),
                    "No double-red violations after inserting " + i + " nodes");
        }
    }

    @Test
    @DisplayName("ValidateRedNodeChildren no double-red violations")
    void testValidateRedNodeChildren_noDoubleRed() {
        // Insert many nodes in different orders to trigger various cases
        int[] insertOrder = {50, 25, 75, 10, 30, 60, 80, 5, 15, 27, 35, 55, 65, 77, 85};

        for (int i : insertOrder) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
            assertTrue(tree.validateRedNodeChildren(),
                    "Should have no double-red violations");
        }
    }

    // ==================== VALIDATE BLACK HEIGHT TESTS ====================

    @Test
    @DisplayName("ValidateBlackHeight passes for empty tree")
    void testValidateBlackHeight_emptyTree() {
        assertTrue(tree.validateBlackHeight(),
                "Empty tree should pass black-height validation");
    }

    @Test
    @DisplayName("ValidateBlackHeight passes after insertions")
    void testValidateBlackHeight_afterInsertions() {
        for (int i = 1; i <= 15; i++) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
            assertTrue(tree.validateBlackHeight(),
                    "All paths should have equal black-height after inserting " + i + " nodes");
        }
    }

    @Test
    @DisplayName("ValidateBlackHeight maintains balance")
    void testValidateBlackHeight_maintainsBalance() {
        // Insert in order that would unbalance regular BST
        for (int i = 1; i <= 20; i++) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
        }

        assertTrue(tree.validateBlackHeight(),
                "Black heights should remain equal even with sequential inserts");
    }

    // ==================== INSERTION CASE TESTS ====================

    @Test
    @DisplayName("Insertion Case 1 - Root becomes black")
    void testInsertionCase1_rootBecomesBlack() {
        // First insertion triggers Case 1
        tree.insert(new Product("P005", "Product5", "Cat5", 50.0));

        assertTrue(tree.validateRootAndLeaves(), "Root should be black (Case 1)");
        assertTrue(tree.validate(), "Tree should be valid");
    }

    @Test
    @DisplayName("Insertion Case 2 - Uncle is red (recoloring)")
    void testInsertionCase2_uncleIsRed() {
        // Build scenario for Case 2
        tree.insert(new Product("P005", "Product5", "Cat5", 50.0)); // Black root
        tree.insert(new Product("P003", "Product3", "Cat3", 30.0)); // Red left
        tree.insert(new Product("P007", "Product7", "Cat7", 70.0)); // Red right

        // This insert should trigger Case 2 (uncle is red)
        tree.insert(new Product("P001", "Product1", "Cat1", 10.0));

        assertTrue(tree.validate(), "Tree should be valid after Case 2");
        assertTrue(tree.validateRedNodeChildren(), "Should have no double-red");
        assertTrue(tree.validateBlackHeight(), "Black heights should be equal");
    }

    @Test
    @DisplayName("Insertion Case 3 - Uncle is black (triangle)")
    void testInsertionCase3_triangleConfiguration() {
        // Build scenario for Case 3
        tree.insert(new Product("P005", "Product5", "Cat5", 50.0));
        tree.insert(new Product("P003", "Product3", "Cat3", 30.0));
        tree.insert(new Product("P007", "Product7", "Cat7", 70.0));
        tree.insert(new Product("P001", "Product1", "Cat1", 10.0));

        // This creates a triangle (left-right) that triggers Case 3
        tree.insert(new Product("P002", "Product2", "Cat2", 20.0));

        assertTrue(tree.validate(), "Tree should be valid after Case 3");
        assertTrue(tree.validateRedNodeChildren(), "Should have no double-red");
    }

    @Test
    @DisplayName("Insertion Case 4 - Uncle is black (line)")
    void testInsertionCase4_lineConfiguration() {
        // Build scenario for Case 4
        tree.insert(new Product("P005", "Product5", "Cat5", 50.0));
        tree.insert(new Product("P003", "Product3", "Cat3", 30.0));
        tree.insert(new Product("P007", "Product7", "Cat7", 70.0));

        // These create line configurations that trigger Case 4
        tree.insert(new Product("P008", "Product8", "Cat8", 80.0));
        tree.insert(new Product("P009", "Product9", "Cat9", 90.0));

        assertTrue(tree.validate(), "Tree should be valid after Case 4");
        assertTrue(tree.validateRedNodeChildren(), "Should have no double-red");
    }

    @Test
    @DisplayName("All insertion cases maintain validity")
    void testAllInsertionCases_maintainValidity() {
        // Insert sequence that triggers all cases
        String[] insertOrder = {
                "P050", "P025", "P075",  // Setup
                "P010", "P030", "P060", "P080",  // Trigger various cases
                "P005", "P015", "P027", "P035",  // More cases
                "P055", "P065", "P077", "P085"   // Complete coverage
        };

        for (String id : insertOrder) {
            tree.insert(new Product(id, "Product", "Cat", 10.0));
            assertTrue(tree.validate(), "Tree should remain valid after inserting " + id);
        }
    }

    // ==================== ROTATION TESTS (indirect via insertion) ====================

    @Test
    @DisplayName("Rotations maintain tree validity")
    void testRotations_maintainValidity() {
        // Sequential inserts force rotations
        for (int i = 1; i <= 7; i++) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
        }

        assertTrue(tree.validate(), "Tree should be valid after rotations");

        // Verify all nodes are still searchable
        for (int i = 1; i <= 7; i++) {
            String id = String.format("P%03d", i);
            assertNotNull(tree.search(id), "Should find product " + id + " after rotations");
        }
    }

    @Test
    @DisplayName("Left rotation scenario")
    void testLeftRotation_scenario() {
        // This sequence should trigger left rotations
        tree.insert(new Product("P001", "Product1", "Cat1", 10.0));
        tree.insert(new Product("P002", "Product2", "Cat2", 20.0));
        tree.insert(new Product("P003", "Product3", "Cat3", 30.0));

        assertTrue(tree.validate(), "Tree should be valid after left rotations");
        assertEquals(3, tree.size(), "Size should be 3");
    }

    @Test
    @DisplayName("Right rotation scenario")
    void testRightRotation_scenario() {
        // This sequence should trigger right rotations
        tree.insert(new Product("P003", "Product3", "Cat3", 30.0));
        tree.insert(new Product("P002", "Product2", "Cat2", 20.0));
        tree.insert(new Product("P001", "Product1", "Cat1", 10.0));

        assertTrue(tree.validate(), "Tree should be valid after right rotations");
        assertEquals(3, tree.size(), "Size should be 3");
    }

    @Test
    @DisplayName("Mixed rotations maintain searchability")
    void testMixedRotations_maintainSearchability() {
        int[] insertOrder = {5, 2, 8, 1, 3, 7, 9, 4, 6};

        for (int i : insertOrder) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
        }

        // All nodes should still be searchable after various rotations
        for (int i : insertOrder) {
            String id = String.format("P%03d", i);
            Product found = tree.search(id);
            assertNotNull(found, "Should find product " + id + " after rotations");
            assertEquals(id, found.getId(), "Should find correct product");
        }
    }

    // ==================== COMPREHENSIVE INTEGRATION TESTS ====================

    @Test
    @DisplayName("Integration test - Sequential operations")
    void integrationTest_sequentialOperations() {
        // Insert
        for (int i = 1; i <= 10; i++) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
        }

        // Verify size and validity
        assertEquals(10, tree.size(), "Should have 10 products");
        assertTrue(tree.validate(), "Tree should be valid");

        // Search all
        for (int i = 1; i <= 10; i++) {
            String id = String.format("P%03d", i);
            assertNotNull(tree.search(id), "Should find product " + id);
        }

        // Update some
        tree.insert(new Product("P005", "UpdatedProduct5", "NewCat", 55.0));
        Product updated = tree.search("P005");
        assertEquals("UpdatedProduct5", updated.getName(), "Should have updated name");

        // Size should not change
        assertEquals(10, tree.size(), "Size should still be 10");
        assertTrue(tree.validate(), "Tree should still be valid");
    }

    @Test
    @DisplayName("Integration test - Random operations")
    void integrationTest_randomOperations() {
        String[] insertOrder = {"P050", "P025", "P075", "P010", "P030", "P060", "P080",
                "P005", "P015", "P027", "P035", "P055", "P065", "P077", "P085"};

        // Insert in random order
        for (String id : insertOrder) {
            tree.insert(new Product(id, "Product" + id, "Cat", 10.0));
            assertTrue(tree.validate(), "Tree should be valid after each insert");
        }

        // Search in different order
        String[] searchOrder = {"P005", "P080", "P027", "P065", "P050", "P010"};
        for (String id : searchOrder) {
            assertNotNull(tree.search(id), "Should find product " + id);
        }

        // Search non-existent
        assertNull(tree.search("P000"), "Should not find P000");
        assertNull(tree.search("P100"), "Should not find P100");
        assertNull(tree.search("P045"), "Should not find P045");

        // Final validation
        assertEquals(15, tree.size(), "Should have 15 products");
        assertTrue(tree.validate(), "Tree should be valid at end");
    }

    @Test
    @DisplayName("Integration test - Large scale")
    void integrationTest_largeScale() {
        int numProducts = 500;

        // Insert many products
        for (int i = 1; i <= numProducts; i++) {
            String id = String.format("P%05d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
        }

        // Verify size
        assertEquals(numProducts, tree.size(), "Should have " + numProducts + " products");

        // Spot check searches
        for (int i = 1; i <= numProducts; i += 50) {
            String id = String.format("P%05d", i);
            assertNotNull(tree.search(id), "Should find product " + id);
        }

        // Validate tree properties
        assertTrue(tree.validate(), "Large tree should be valid");
        assertTrue(tree.validateRootAndLeaves(), "Root should be black");
        assertTrue(tree.validateRedNodeChildren(), "Should have no double-red");
        assertTrue(tree.validateBlackHeight(), "Black heights should be equal");

        // Verify height is logarithmic
        int height = tree.height();
        int maxHeight = (int) (2 * Math.log(numProducts + 1) / Math.log(2)) + 1;
        assertTrue(height <= maxHeight,
                String.format("Height %d should be <= %d for %d nodes", height, maxHeight, numProducts));
    }

    @Test
    @DisplayName("Integration test - All validation functions pass together")
    void integrationTest_allValidationsPass() {
        for (int i = 1; i <= 25; i++) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));

            // All validations should pass after each insert
            assertTrue(tree.validateRootAndLeaves(),
                    "Root/leaves validation should pass at size " + i);
            assertTrue(tree.validateNodeColors(),
                    "Color validation should pass at size " + i);
            assertTrue(tree.validateRedNodeChildren(),
                    "Red-node validation should pass at size " + i);
            assertTrue(tree.validateBlackHeight(),
                    "Black-height validation should pass at size " + i);
            assertTrue(tree.validate(),
                    "Comprehensive validation should pass at size " + i);
        }
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    @DisplayName("Edge case - Single node tree")
    void edgeCase_singleNode() {
        tree.insert(new Product("P001", "Product1", "Cat1", 10.0));

        assertEquals(1, tree.size(), "Size should be 1");
        assertEquals(1, tree.height(), "Height should be 1");
        assertNotNull(tree.search("P001"), "Should find the single node");
        assertTrue(tree.validate(), "Single node tree should be valid");
    }

    @Test
    @DisplayName("Edge case - Two node tree")
    void edgeCase_twoNodes() {
        tree.insert(new Product("P001", "Product1", "Cat1", 10.0));
        tree.insert(new Product("P002", "Product2", "Cat2", 20.0));

        assertEquals(2, tree.size(), "Size should be 2");
        assertNotNull(tree.search("P001"), "Should find first node");
        assertNotNull(tree.search("P002"), "Should find second node");
        assertTrue(tree.validate(), "Two node tree should be valid");
    }

    @Test
    @DisplayName("Edge case - All same category")
    void edgeCase_sameCategory() {
        for (int i = 1; i <= 10; i++) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "SameCategory", 10.0 * i));
        }

        assertEquals(10, tree.size(), "Size should be 10");
        assertTrue(tree.validate(), "Tree should be valid");
    }

    @Test
    @DisplayName("Edge case - Identical prices")
    void edgeCase_identicalPrices() {
        for (int i = 1; i <= 10; i++) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat" + i, 99.99));
        }

        assertEquals(10, tree.size(), "Size should be 10");
        assertTrue(tree.validate(), "Tree should be valid");

        // Verify all have same price
        for (int i = 1; i <= 10; i++) {
            String id = String.format("P%03d", i);
            Product found = tree.search(id);
            assertEquals(99.99, found.getPrice(), 0.01, "All should have same price");
        }
    }

    @Test
    @DisplayName("Edge case - Very similar IDs")
    void edgeCase_similarIds() {
        tree.insert(new Product("P001", "Product1", "Cat1", 10.0));
        tree.insert(new Product("P011", "Product11", "Cat11", 110.0));
        tree.insert(new Product("P101", "Product101", "Cat101", 1010.0));

        assertEquals(3, tree.size(), "Size should be 3");
        assertNotNull(tree.search("P001"), "Should find P001");
        assertNotNull(tree.search("P011"), "Should find P011");
        assertNotNull(tree.search("P101"), "Should find P101");
        assertTrue(tree.validate(), "Tree should be valid");
    }

    // ==================== STRESS TESTS ====================

    @Test
    @DisplayName("Stress test - 1000 sequential inserts")
    void stressTest_1000SequentialInserts() {
        for (int i = 1; i <= 1000; i++) {
            String id = String.format("P%06d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
        }

        assertEquals(1000, tree.size(), "Size should be 1000");
        assertTrue(tree.validate(), "Tree should be valid after 1000 inserts");

        // Spot check
        assertNotNull(tree.search("P000001"), "Should find first product");
        assertNotNull(tree.search("P000500"), "Should find middle product");
        assertNotNull(tree.search("P001000"), "Should find last product");
    }

    @Test
    @DisplayName("Stress test - Many updates")
    void stressTest_manyUpdates() {
        // Insert 100 products
        for (int i = 1; i <= 100; i++) {
            String id = String.format("P%03d", i);
            tree.insert(new Product(id, "Product" + i, "Cat", 10.0 * i));
        }

        // Update each product 5 times
        for (int j = 1; j <= 5; j++) {
            for (int i = 1; i <= 100; i++) {
                String id = String.format("P%03d", i);
                tree.insert(new Product(id, "Updated" + j, "Cat", 10.0 * i * j));
            }
        }

        assertEquals(100, tree.size(), "Size should still be 100");
        assertTrue(tree.validate(), "Tree should be valid after many updates");

        // Verify final state
        Product p = tree.search("P050");
        assertEquals("Updated5", p.getName(), "Should have final update");
    }
}