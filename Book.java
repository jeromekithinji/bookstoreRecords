/**
 * COMP 6481 (Winter 2024) 
 * Assignment #2
 * Due Date: Sunday, March 24th, 2024.
 * 
 * © Oluwamosope Adeyemi. ID: 40293741
 * © Jerome Kithinji. ID: 40280348
 * 
 * Written by: 
 * Jerome Kithinji. ID: 40280348
 * Oluwamosope Adeyemi. ID: 40293741
 */

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a book with title, author, price, ISBN, genre, and publication year.
 */
public class Book implements Serializable {
    String title;
    String author;
    double price;
    String isbn;
    String genre;
    int year;

    /**
     * Constructs a Book instance with specified details.
     *
     * @param title  The title of the book.
     * @param author The author of the book.
     * @param price  The price of the book.
     * @param isbn   The ISBN number of the book.
     * @param genre  The genre of the book.
     * @param year   The year of publication of the book.
     */
    public Book(String title, String author, double price, String isbn, String genre, int year) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.isbn = isbn;
        this.genre = genre;
        this.year = year;
    }

    /**
     * Compares this book to another book object. The result is true if and only if
     * the argument is not null and the books have the same attributes.
     *
     * @param obj the other Book to compare this Book against
     * @return true if the books are the same, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Book other = (Book) obj;
        return Double.compare(other.price, price) == 0 &&
                year == other.year &&
                Objects.equals(title, other.title) &&
                Objects.equals(author, other.author) &&
                Objects.equals(isbn, other.isbn) &&
                Objects.equals(genre, other.genre);
    }

    /**
     * Returns a string representation of the book with its properties.
     *
     * @return A string detailing the book's title, author, price, ISBN, genre, and year.
     */
    @Override
    public String toString() {
        return "Title: " + this.title + " Author: " + this.author + " Price: " + this.price + " ISBN: " + this.isbn
                + " Genre: " + this.genre + " Year: " + this.year;
    }

    /**
     * Gets the title of the book.
     *
     * @return The title of the book.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     *
     * @param title The title to set for the book.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the author of the book.
     *
     * @return The author of the book.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the book.
     *
     * @param author The author to set for the book.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the price of the book.
     *
     * @return The price of the book.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price of the book.
     *
     * @param price The price to set for the book.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the ISBN of the book.
     *
     * @return The ISBN of the book.
     */
    public String getIsbn() {
        return isbn;
    }

     /**
     * Sets the ISBN of the book.
     *
     * @param isbn The ISBN to set for the book.
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the genre of the book.
     *
     * @return The genre of the book.
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets the genre of the book.
     *
     * @param genre The genre to set for the book.
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Gets the publication year of the book.
     *
     * @return The publication year of the book.
     */
    public int getYear() {
        return year;
    }

    /**
     * Sets the publication year of the book.
     *
     * @param year The publication year to set for the book.
     */
    public void setYear(int year) {
        this.year = year;
    }

}