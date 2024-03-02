import java.io.Serializable;
import java.util.Objects;

class Book implements Serializable {
    String title;
    String author;
    double price;
    String isbn;
    String genre;
    int year;

    /**
     * Condtructs a Book object.
     * @param title
     * @param author
     * @param price
     * @param isbn
     * @param genre
     * @param year
     */
    public Book(String title, String author, double price, String isbn, String genre, int year) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.isbn = isbn;
        this.genre = genre;
        this.year = year;
    }
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

    @Override
    public String toString() {
        return "Title: " + this.title + " Author: " + this.author + " Price: " + this.price + " ISBN: " + this.isbn
                + " Genre: " + this.genre + " Year: " + this.year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

}