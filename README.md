# Bookstore Record Management System

This project is a Java-based system designed to help manage and validate a large collection of bookstore records. It was developed as part of an academic assignment for COMP6481 (Winter 2024) and focuses on exception handling, file I/O, and interactive user interfaces.

---

## About the Scenario

Mr. Booker, a retired bookstore employee, has a catalog of almost 2000 book entries stored in multiple text files. These files are categorized by genre but contain various syntax and semantic errors. Mr. Booker seeks a solution to:
- Validate and categorize book records.
- Identify and handle errors in the records.
- Provide an interactive way to navigate the cleaned and categorized records.

---

## Task Overview

This project is implemented in three sequential parts:

### **Part 1: Syntax Validation and File Categorization**
- Reads multiple CSV-formatted text files containing book records.
- Validates the syntax of each record (e.g., missing fields, unknown genres).
- Categorizes valid records into separate files based on genre.
- Logs syntax errors into a separate error file.

### **Part 2: Semantic Validation and Binary Serialization**
- Reads the genre-based files generated in Part 1.
- Validates the semantic correctness of each record (e.g., valid ISBNs, price, and year).
- Converts semantically valid records into `Book` objects.
- Serializes these objects into binary files for efficient storage.

### **Part 3: Interactive Navigation**
- Deserializes the binary files generated in Part 2.
- Provides an interactive menu to navigate and view records in each category.
- Allows users to move through records in specific ranges or select different categories for viewing.

---

## Features

- **Validation Mechanisms**:
  - Detect and report syntax errors like missing fields or invalid genres.
  - Identify semantic errors like incorrect ISBNs, invalid prices, or years outside the valid range.
- **Categorization**:
  - Organize valid book records into genre-specific files.
- **Interactive User Interface**:
  - Navigate through records interactively with menu-driven options.
- **Error Handling**:
  - Implements custom exceptions for specific error types (e.g., `TooManyFieldsException`, `BadIsbn10Exception`).

---

## Learning Objectives

This project helped to develop skills in:
- **File I/O**: Reading, writing, and managing both text and binary files.
- **Exception Handling**: Designing and using custom exceptions to manage errors.
- **Object-Oriented Design**:
  - Designing and implementing Java classes like `Book` with proper encapsulation.
  - Using serialization for efficient storage and retrieval.
- **Interactive Programming**: Creating user-friendly interfaces for real-time record navigation.
- **Data Validation**: Checking both syntax and semantics of structured data.
- **JavaDoc Documentation**: Writing professional documentation for code.

---
