# A4 — E-commerce (CS 2341, Fall 2025)

**Points:** Part 1 (30) + Part 2 (50) + Part 3 (20) = **100**  
**Due:** TBD (Check course schedule)  
**Late policy:** 1 day: −10%; 2 days: −20%; 3 days: −30%; >10 days: no credit.

---

## Assignment Focus

You will develop a data management system for a large e-commerce platform that handles product records efficiently. The system reads CSV data, parses complex fields (product names with quotes, categories with pipes), and stores products in a **Red-Black tree** for fast insertion and retrieval.

* **Part 1:** Parse `amazon-product-data.csv` — handle quoted fields, escaped quotes, and pipe-separated categories
* **Part 2:** Implement a **Red-Black tree** to store and search product records by `product-id`
* **Part 3:** Analyze **time complexity** and discuss how the tree maintains balance during operations

> This assignment emphasizes understanding balanced search trees, CSV parsing edge cases, and performance analysis.

---

## Global Rules

* **Allowed libraries:** Basic Java + any CSV parsing utilities you implement yourself.
* **Forbidden:** Do not use external CSV libraries like OpenCSV or Apache Commons CSV. Implement your own parser.
* **Data structures:** You must implement your own **Red-Black tree** from scratch. Do not use Java's `TreeMap` or other built-in balanced trees.
* **Filenames (one public class per file):**
    * `Product.java` (product record class)
    * `CsvParser.java` or similar (Part 1)
    * `RedBlackTree.java` (Part 2)
    * `ProductManager.java` or `Main.java` (driver program)

---

# Part 1 — CSV Parsing (30 pts)

### Introduction

Parse the `amazon-product-data.csv` file to extract product information. Each line represents a product with comma-separated values. Special handling is required for:

1. **Product names with commas** — enclosed in double quotes
2. **Categories with pipes** — multiple categories separated by `|`
3. **Escaped quotes** — two consecutive quotes (`""`) represent a literal quote (`"`)

Your parser must correctly handle these edge cases and extract clean data for each product.

---

### CSV Format Specification

**Format:** `product-id,product-name,category,price`

**Field descriptions:**
* `product-id`: Unique identifier (string or integer)
* `product-name`: Name of product (may contain commas, enclosed in quotes)
* `category`: Product categories separated by `|` (may be quoted)
* `price`: Decimal price value

**Special cases:**

1. **Quoted fields:**
   ```csv
   12345,"Samsung 55-Inch TV, 4K UHD",Electronics|Home Entertainment,799.99
   ```
   The comma in "Samsung 55-Inch TV, 4K UHD" doesn't split the field because it's quoted.

2. **Escaped quotes within fields:**
   ```csv
   67890,"Apple 15"" MacBook Pro",Computers|Laptops,2399.99
   ```
   The `""` represents a literal quote: `Apple 15" MacBook Pro`

3. **Pipe-separated categories:**
   ```csv
   11111,Bluetooth Speaker,Audio|Electronics|Portable Devices,49.99
   ```
   Three categories: `Audio`, `Electronics`, `Portable Devices`

---

### Requirements

**Class:** `CsvParser` (or integrate into your `ProductManager`)

**Method signature:**
```java
public static List<Product> parseCsv(String filepath) throws IOException {
    // Read file line by line
    // Parse each line considering quotes and escapes
    // Return list of Product objects
}
```

**Implementation notes:**
* Do **not** use external CSV libraries; implement your own parser
* Handle quoted fields that may contain commas
* Process escaped quotes (`""` → `"`)
* Split categories by pipe (`|`) and store as appropriate structure
* Validate that each line has exactly 4 fields (after proper parsing)

**Error handling:**
* Skip or log lines with incorrect number of fields
* Handle empty lines gracefully
* Report malformed quotes if detected

---

### Sample Input

**amazon-product-data.csv:**
```csv
1001,Wireless Mouse,Electronics|Accessories,29.99
1002,"Apple iPhone 14, 128GB",Smartphones|Electronics,999.99
1003,"Book: ""The Art of War""",Books|Classics,14.99
1004,Gaming Keyboard,Electronics|Gaming|Accessories,79.99
1005,"Sony 65"" OLED TV, Smart",Electronics|Home Entertainment|TVs,1899.99
```

---

### Sample Output (Parsed Products)

After parsing, your system should have extracted:

```
Product ID: 1001
Name: Wireless Mouse
Category: Electronics|Accessories
Price: 29.99

Product ID: 1002
Name: Apple iPhone 14, 128GB
Category: Smartphones|Electronics
Price: 999.99

Product ID: 1003
Name: Book: "The Art of War"
Category: Books|Classics
Price: 14.99

Product ID: 1004
Name: Gaming Keyboard
Category: Electronics|Gaming|Accessories
Price: 79.99

Product ID: 1005
Name: Sony 65" OLED TV, Smart
Category: Electronics|Home Entertainment|TVs
Price: 1899.99
```

**Note:** Outer quotes are removed, but escaped quotes within the text are converted to single quotes.

---

### Explanation (Parsing Logic)

1. **Line 1:** No quotes → simple split by comma
2. **Line 2:** Product name is quoted (contains comma) → extract text between quotes, then split remaining fields
3. **Line 3:** Product name contains escaped quotes (`""`) → convert to literal quote (`"`)
4. **Line 5:** Product name contains a quote character (`65"`) → handle as escaped sequence

**Parsing algorithm (simplified):**
```
For each line:
  1. Check if field starts with quote
  2. If quoted:
     - Find closing quote (not preceded by another quote)
     - Extract content, replace "" with "
  3. If not quoted:
     - Split by comma normally
  4. Separate categories by pipe (|)
```

---

### Constraints

* CSV file may contain 1 to 100,000 products
* Product IDs are unique
* Prices are positive decimal values
* Category field may contain 1-10 categories separated by pipes

---

### Testing Your Parser

Create a small test CSV with edge cases:
```csv
1,"Product with, comma","Category1|Category2",10.00
2,"Product with ""quotes""",Category3,20.00
3,Simple Product,Category4|Category5|Category6,30.00
```

Your parser should correctly handle all three cases.

---

# Part 2 — Left-Leaning Red-Black Tree Implementation (50 pts)

### Introduction

Implement a **Left-Leaning Red-Black (LLRB) tree** to store product records, indexed by `product-id`. This elegant variant of Red-Black trees, developed by Robert Sedgewick at Princeton, maintains 2-3 tree semantics while providing the same O(log n) guarantees as standard Red-Black trees with significantly simpler code.

**Reference:** Princeton's Algorithms 4th Edition - https://algs4.cs.princeton.edu/33balanced/index.php#3.4

LLRB trees guarantee `O(log n)` worst-case time for insertion and search operations through a simplified balancing approach that enforces three key invariants.

---

### Left-Leaning Red-Black Tree Invariants

Your implementation must maintain these LLRB invariants (which correspond to 2-3 tree properties):

1. **No right-leaning red links** — Red links always lean to the left
2. **No node has two red children** — No consecutive red links on any path
3. **Perfect black balance** — Every path from root to leaf contains the same number of black links
4. **Root is always black** — Enforced after every insertion

**Why LLRB over standard RB trees?**
- Simpler implementation (about half the code)
- Maintains perfect 1-1 correspondence with 2-3 trees
- Uses only three simple operations applied in a fixed order
- No need to track parent pointers or handle multiple cases

---

### Function/Class Description

**Class: `RedBlackTree`**

```java
public class RedBlackTree {
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    
    private Node root;
    private int size;
    
    // Inner class for tree nodes
    private static class Node {
        String productId;      // Key for comparison
        Product product;       // Full product data
        Node left, right;      // No parent pointer needed!
        boolean color;         // Color: RED = true, BLACK = false
        
        Node(Product product, boolean color) {
            this.productId = product.getId();
            this.product = product;
            this.color = color;
            this.left = null;
            this.right = null;
        }
    }
    
    // Required methods
	public RedBlackTree()
    public void insert(Product product) { }
    public Product search(String productId) { }
    public boolean validate() {}
    public boolean is23() {}

    // Helpper functions
    public int size() {}
    public boolean isEmpty() {}
    public int height() {}
    public int countRedLinks() {}
    public void printTree() {}

}
```

---

### Required Operations

#### 1. Insert Operation

**Signature:**
```java
public void insert(Product product)
```

**LLRB Algorithm (beautifully simple!):**
1. Perform standard BST insertion using `product-id` as key — insert new nodes as **RED**
2. After each recursive call returning from insertion, apply these three operations **in order**:

   a. **Rotate left** if right child is red and left child is black
      ```java
      if (isRed(h.right) && !isRed(h.left)) 
          h = rotateLeft(h);
      ```

   b. **Rotate right** if left child and left-left grandchild are both red
      ```java
      if (isRed(h.left) && isRed(h.left.left)) 
          h = rotateRight(h);
      ```

   c. **Flip colors** if both children are red (split a 4-node)
      ```java
      if (isRed(h.left) && isRed(h.right)) 
          flipColors(h);
      ```

3. Set root to black after insertion completes

**Key insight:** These three simple operations, applied in this exact order on the way up the recursion, automatically maintain all LLRB invariants. No complex case analysis needed!

**Time complexity:** `O(log n)`

---

#### 2. Search Operation

**Signature:**
```java
public Product search(String productId)
```

**Algorithm:**
1. Start at root
2. Compare `productId` with current node's key
3. Go left if smaller, right if larger
4. Return product when found, or `null` if not found

**Note:** Search is identical to standard BST search — colors don't affect search logic.

**Time complexity:** `O(log n)`

---

### LLRB Balancing Operations

#### Left Rotation
Make a right-leaning red link lean left:
```
    h (E)                x (S)
   / \                  / \
  a   x (S)     =>   h (E) c
     / \            / \
    b   c          a   b
```
- **Purpose:** Eliminate right-leaning red links
- **Implementation:** Node `x` becomes new root, inherits `h`'s color; `h` becomes red

#### Right Rotation
Balance two left-leaning red links:
```
      h (S)            x (E)
     / \              / \
  x (E) c      =>    a   h (S)
   / \                  / \
  a   b                b   c
```
- **Purpose:** Fix consecutive left-leaning red links
- **Implementation:** Node `x` becomes new root, inherits `h`'s color; `h` becomes red

#### Color Flip
Split a 4-node (both children red):
```
      h (B)                h (R)
     / \                  / \
  l (R) r (R)    =>    l (B) r (B)
```
- **Purpose:** Push red link up the tree to maintain 2-3 tree semantics
- **Implementation:** Flip colors of node and both children

**Critical:** These operations preserve perfect black balance!


---

### Output Format for Search

When displaying a found product, print all fields:

```
Product ID: 1002
Name: Apple iPhone 14, 128GB
Category: Smartphones|Electronics
Price: $999.99
```

If product not found:
```
Product ID: 9999 not found.
```

---

### Constraints

* Implement all rotations and color adjustments yourself
* Do **not** use Java's built-in `TreeMap` or `TreeSet`
* Handle duplicate IDs by updating the existing product (or rejecting the insertion)
* Tree must pass LLRB invariant checks (optional: implement a validation method)

---

# Part 3 — Performance Analysis Report (20 pts)

### Introduction

Write a **written performance analysis** that demonstrates your understanding of Red-Black tree behavior and performance characteristics. This part requires **no programming**, but thoughtful analysis based on your implementation and observations.

---

### Report Requirements

**File:** `performance_analysis.txt` (plaintext format)  
**Location:** `src/main/resources/performance_analysis.txt`

---

### Required Analysis Topics

Your report must address the following areas. Use specific observations from your implementation to support your analysis.

#### 1. Time Complexity Analysis (8 pts)

Analyze the time complexity of your Red-Black tree operations:

**For Insert Operation:**
- What is the theoretical worst-case time complexity?
- Why does the tree height matter for insert performance?
- What are the different steps in insertion (BST insert, fixup, rotations)?
- How does each step contribute to the overall time complexity?
- How does Red-Black tree insertion compare to an unbalanced BST?

**For Search Operation:**
- What is the theoretical worst-case time complexity?
- What factors determine search performance?
- How does tree height affect search time?
- Compare search performance to other data structures (arrays, hash tables)

**Your Task:** Explain the time complexity of each operation with clear reasoning. Support your analysis with the theoretical bounds and explain why these bounds hold.

---

#### 2. Tree Balance and Maintenance (8 pts)

Discuss how the Red-Black tree maintains balance:

**Balancing Mechanism:**
- What are the five Red-Black tree properties?
- How do these properties ensure the tree remains balanced?
- What is "black-height" and why is it important?
- Why does maintaining black-height prevent the tree from degenerating into a linked list?

**Insertion and Rebalancing:**
- What happens after a standard BST insertion?
- What violations can occur?
- How does the fixup process restore balance?
- What role do rotations play in maintaining balance?
- How many rotations might be needed in the worst case?

**Performance Impact:**
- What is the cost of maintaining balance?
- When is this cost justified compared to unbalanced trees?
- How does balancing affect insert vs search performance?

**Your Task:** Explain how the tree stays balanced through insertions. Discuss the trade-offs between the cost of balancing and the benefits it provides.

---

#### 3. Practical Performance Observations (4 pts)

Discuss practical considerations based on your implementation:

**Scalability:**
- How does performance change as the dataset grows (100 → 10,000 → 100,000 products)?
- What tree heights did you observe for different dataset sizes?
- Do your observations match the theoretical maximum height of `2 log₂(n+1)`?

**Real-World Considerations:**
- What are the memory requirements (space complexity)?
- When would you choose a Red-Black tree over:
    - A hash table?
    - An AVL tree?
    - An array with binary search?
- What types of applications benefit most from Red-Black trees?

**Your Task:** Provide practical insights about when and why to use Red-Black trees. Include observations from running your implementation with different dataset sizes if possible.

---

## Deliverables Summary

Submit your assignment through **GitHub Classroom**. All files must be properly organized in your repository.

### Repository Structure

```
a4-product-management/
├── src/
│   ├── main/
│   │   ├── java/com/student_work/
│   │   │   ├── Product.java
│   │   │   ├── CsvParser.java
│   │   │   ├── RedBlackTree.java
│   │   │   └── ProductManager.java (or Main.java)
│   │   └── resources/
│   │       ├── amazon-product-data.csv     (provided dataset)
│   │       ├── search_results.txt          (output from 3 searches)
│   │       └── performance_analysis.txt    (your written analysis)
│   └── test/
│       └── java/com/student_work/
│           └── (optional: your test files)
├── data/
│   └── amazon-product-data.csv             (provided dataset)
└── README.md                                (optional: project documentation)
```

---

### How to Submit

1. **Accept the GitHub Classroom assignment** (link provided by instructor)
2. **Clone your repository** to your local machine:
   ```bash
   git clone <your-repo-url>
   cd a4-product-management
   ```
3. **Implement your solution** following the structure above
4. **Test thoroughly** - ensure all parts work correctly
5. **Generate output files** - run your program to create search_results.txt
6. **Commit and push** your code to GitHub:
   ```bash
   git add src/ data/
   git commit -m "Complete Assignment 4: Product Management with Red-Black Tree"
   git push origin main
   ```
7. **Verify submission** - check GitHub to ensure all files are visible and in correct locations

**Important:** The timestamp of your final push to GitHub will be used as your submission time for late penalties.
