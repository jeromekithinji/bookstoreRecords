import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Exceptions.BadIsbn10Exception;
import Exceptions.BadIsbn13Exception;
import Exceptions.BadPriceException;
import Exceptions.BadYearException;

public class BookManager {
    public static void main(String args[]) {
        do_part2();
    }

    static void do_part1() {

    }

    /**
     * Reads the genre-based CVS-formatted input text files produced in Part 1, one
     * file at a time, creating an array of valid Book objects out of all the
     * semantically valid book records in each input file, then serializes the
     * resulting array of Book objects into the genre's binary file
     */
    static void do_part2() {
        String[] p1OutputFiles = {
                "Cartoons_Comics.csv",
                "Hobbies_Collectibles.csv",
                "Movies_TV_Books.csv",
                "Music_Radio_Books.csv",
                "Nostalgia_Eclectic_Books.csv",
                "Old_Time_Radio_Books.csv",
                "Sports_Sports_Memorabilia.csv",
                "Trains_Planes_Automobiles.csv"
        };

        for (int i = 0; i < p1OutputFiles.length; i++) {
            ArrayList<Book> validBooks = new ArrayList<Book>();

            try (Scanner scanner = new Scanner(
                    new FileInputStream("part1_output_files" + File.separator + p1OutputFiles[i]))) {

                boolean firstErrorFoundInFile = false;

                while (scanner.hasNextLine()) {
                    String bookRecord = scanner.nextLine();
                    String[] splitBookRecord = bookRecord.split(",");

                    String title = splitBookRecord[0].trim();
                    String author = splitBookRecord[1].trim();
                    double price = Double.parseDouble(splitBookRecord[2].trim());
                    String isbn = splitBookRecord[3].trim();
                    String genre = splitBookRecord[4].trim();
                    int year = Integer.parseInt(splitBookRecord[5].trim());

                    Boolean isValidIsbn = false;
                    Boolean isValidYear = false;
                    Boolean isValidPrice = false;

                    if (isbn.length() == 10)
                        isValidIsbn = isValidIsbn10(isbn);
                    else if (isbn.length() == 13)
                        isValidIsbn = isValidIsbn13(isbn);

                    isValidYear = isValidYear(year);
                    isValidPrice = isValidPrice(price);

                    String errorPrefix = firstErrorFoundInFile == false
                            ? "Semantic error in file: " + p1OutputFiles[i] + "\n====================\n"
                            : "";

                    if (!isValidIsbn || !isValidPrice || !isValidYear)
                        firstErrorFoundInFile = true;

                    if (!isValidIsbn && isbn.length() == 10) {
                        logSemanticExceptionToFile(
                                errorPrefix + "Error: invalid ISBN-10\nRecord: " + bookRecord + "\n");
                    } else if (!isValidIsbn && isbn.length() == 13) {
                        firstErrorFoundInFile = true;
                        logSemanticExceptionToFile(
                                errorPrefix + "Error: invalid ISBN-13\nRecord: " + bookRecord + "\n");
                    }

                    // checks if year is valid
                    if (!isValidYear) {
                        logSemanticExceptionToFile(errorPrefix + "Error: invalid year\nRecord: " + bookRecord + "\n");
                    }
                    // checks if price is valid
                    if (!isValidPrice) {
                        logSemanticExceptionToFile(errorPrefix + "Error: invalid price\nRecord: " + bookRecord + "\n");
                    }

                    // checks if the book has all valid fields and creates a Book object, then
                    // pushes the object into the Book array
                    if (isValidIsbn && isValidPrice && isValidYear) {
                        Book bookObj = new Book(title, author, price, isbn, genre, year);
                        validBooks.add(bookObj);
                    }
                }

                // Write the array of valid books to their respective genre binary files
                try (ObjectOutputStream outputStream = new ObjectOutputStream(
                        new FileOutputStream("part2_output_files" + File.separator + p1OutputFiles[i] + ".ser"))) {
                    outputStream.writeObject(validBooks);
                } catch (FileNotFoundException e) {
                    System.out.println("Unable to create " + p1OutputFiles[i] + " binary File.");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("Unable to write to " + p1OutputFiles[i] + " binary File.");
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e) {
                System.out.println("File not found");
                e.printStackTrace();
            }
        }
    }

    static void do_part3() {

    }

    private static void logSemanticExceptionToFile(String error) {
        try (PrintWriter writer = new PrintWriter(
                new FileOutputStream("error_logs" + File.separator + "semantic_error_file.txt", true))) {
            writer.println(error);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open semantic_error_file.txt");
            e.printStackTrace();
        }
    }

    static boolean isValidPrice(double price) {
        return price > 0 ? true : false;
    }

    static boolean isValidYear(int year) {
        return (year >= 1995 && year <= 2024);
    }

    /**
     * Validates the semantic correctness of the ISBN 10.
     * 
     * @param isbn the string ISBN of the book to validate
     * @return True if ISBN 10 is valid, false otherwise.
     */
    static boolean isValidIsbn10(String isbn) {
        if (isbn.length() != 10)
            return false;
        int isbnSum = 0;

        for (int i = 10; i > 0; i--) {
            isbnSum += i * Integer.parseInt(Character.toString(isbn.charAt(isbn.length() - i)));
        }
        return isbnSum % 11 == 0;
    }

    /**
     * Validates the semantic correctness of the isbn13 argument.
     * 
     * @param isbn the ISBN of the book to validate
     * @return True if ISBN 13 is valid, false otherwise
     */
    static boolean isValidIsbn13(String isbn) {
        if (isbn.length() != 13)
            return false;
        int isbnSum = 0;

        for (int i = 1; i <= isbn.length(); i++) {
            if (i % 2 == 0) {
                isbnSum += 3 * Integer.parseInt(Character.toString(isbn.charAt(i - 1)));
            } else {
                isbnSum += Integer.parseInt(Character.toString(isbn.charAt(i - 1)));
            }
        }
        return isbnSum % 10 == 0;
    }
}