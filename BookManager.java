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

    /**
     * Reads input files containing book records, validates each record, 
     * and adds valid records to output files. Logs syntax errors to a 
     * separate error log file.
     */
    static void do_part1() {
        String bookRecordsNames[] = new String[0];
        Scanner scanner = null;

        // read input files file
        try {
            scanner = new Scanner(new FileInputStream("Part1_input_file_names.txt"));
            int numberOfFiles = scanner.nextInt();
            scanner.nextLine();

            bookRecordsNames = new String[numberOfFiles];
            for (int i = 0; i < numberOfFiles; i++) {
                bookRecordsNames[i] = scanner.nextLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        // read the book record files
        for (int i = 0; i < bookRecordsNames.length; i++) {
            try {
                String errorMessage = "";
                boolean firstError = true;
                scanner = new Scanner(new FileInputStream(bookRecordsNames[i]));
                while (scanner.hasNext()) {
                    String bookRecord = scanner.nextLine();
                    String bookRecordTokens[] = tokenizeBookRecord(bookRecord);
                    if (validateBookRecord(bookRecordsNames[i], bookRecordTokens, bookRecord, firstError)) {
                        addRecordToFile(bookRecord, bookRecordTokens[4]);
                    } else {
                        firstError = false;
                    }
                }
                firstError = true;
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            }
        }
        scanner.close();
    }

    /**
    * Adds the given book record to the specified genre output file.
    *
    * @param bookRecord the book record to add
    * @param genre      the genre of the book record
    */
    static void addRecordToFile(String bookRecord, String genre) {
        String fileName;
        switch (genre) {
            case "CCB":
                fileName = "Cartoons_Comics.csv";
                break;
            case "HCB":
                fileName = "Hobbies_Collectibles.csv";
                break;
            case "MTV":
                fileName = "Movies_TV_Books.csv";
                break;
            case "MRB":
                fileName = "Music_Radio_Books.csv";
                break;
            case "NEB":
                fileName = "Nostalgia_Eclectic_Books.csvOld_Time_Radio_Books.csv";
                break;
            case "OTR":
                fileName = "Old_Time_Radio_Books.csv";
                break;
            case "SSM":
                fileName = "Sports_Sports_Memorabilia.csv";
                break;
            case "TPA":
                fileName = "Trains_Planes_Automobiles.csv";
                break;
            default:
                System.out.println("Error: Invalid genre");
                return;
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream("part1_output_files" + File.separator + fileName, true));
            writer.println(bookRecord);
        } catch (IOException e) {
            System.out.println("Error could not write book record");
        }

        writer.close();
    }

    /**
     * Logs a syntax error to the error log file.
     *
     * @param error the error message
     * @param firstError a flag indicating if this is the first error for the file
     * @param fileName the name of the file containing the error
     */
    private static void logSyntaxErrorToFile(String error, boolean firstError, String fileName) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(
                    new FileOutputStream("error_logs" + File.separator + "syntax_error_file.txt", true));
            if (firstError) {
                writer.println("syntax error in file: " + fileName);
                writer.println("====================");
                writer.println(error);
            } else {
                writer.println(error);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Unable to wrrite to syntax_error_file.txt");
            e.printStackTrace();
        }
        writer.close();
    }

    /**
     * Validates the given book record for syntax errors.
     *
     * @param bookRecordFile the name of the input file containing the book record
     * @param bookRecordTokens the tokens of the book record
     * @param bookRecord the book record
     * @param firstError a flag indicating if this is the first error for the file
     * @return true if the book record is valid, false otherwise
     */
    static boolean validateBookRecord(String bookRecordFile, String[] bookRecordTokens, String bookRecord,
            boolean firstError) {
        if (bookRecordTokens.length > 6) {
            logSyntaxErrorToFile("Error: too many fields\nRecord: " + bookRecord + "\n", firstError, bookRecordFile);
            return false;
        } else if (bookRecordTokens.length < 5) {
            logSyntaxErrorToFile("Error: too few fields\nRecord: " + bookRecord + "\n", firstError, bookRecordFile);
            return false;
        } else {
            if (!validateMissingFields(bookRecordTokens, bookRecord, bookRecordFile, firstError)) {
                return false;
            } else {
                return isGenreValid(bookRecordTokens, bookRecord, bookRecordFile, firstError);
            }
        }
    }

    /**
     * Validates missing fields in the book record.
     *
     * @param bookRecordTokens the tokens of the book record
     * @param bookRecord the book record
     * @param bookRecordFile the name of the input file containing the book record
     * @param firstError a flag indicating if this is the first error for the file
     * @return true if all required fields are present, false otherwise
     */
    static boolean validateMissingFields(String[] bookRecordTokens, String bookRecord, String bookRecordFile,
            boolean firstError) {
        boolean missingField = false;
        String missingFields = "";
        for (int j = 0; j < bookRecordTokens.length; j++) {
            if (bookRecordTokens[j].isEmpty()) { // should we consider if there is an empty space character as well?
                missingField = true;
                switch (j) {
                    case 0:
                        missingFields += "title ";
                        break;
                    case 1:
                        missingFields += "authors ";
                        break;
                    case 2:
                        missingFields += "price ";
                        break;
                    case 3:
                        missingFields += "isbn ";
                        break;
                    case 4:
                        missingFields += "genre ";
                        break;
                    case 5:
                        missingFields += "year ";
                        break;
                }
            }
        }
        if (missingField) {
            logSyntaxErrorToFile("Error: missing " + missingFields + "\nRecord: " + bookRecord + "\n", firstError,
                    bookRecordFile);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Validates the genre of the book record.
     *
     * @param bookRecordTokens the tokens of the book record
     * @param bookRecord the book record
     * @param bookRecordFile the name of the input file containing the book record
     * @param firstError a flag indicating if this is the first error for the file
     * @return true if the genre is valid, false otherwise
     */
    static boolean isGenreValid(String[] bookRecordTokens, String bookRecord, String bookRecordFile,
            boolean firstError) {
        String[] validGenres = { "CCB", "HCB", "MTV", "MRB", "NEB", "OTR", "SSM", "TPA" };
        boolean isValidGenre = false;
        for (String validGenre : validGenres) {
            if (validGenre.equals(bookRecordTokens[4])) {
                isValidGenre = true;
                break;
            }
        }
        if (!isValidGenre) {
            logSyntaxErrorToFile("Error: invalid genre\nRecord: " + bookRecord + "\n", firstError, bookRecordFile);
            return false;
        }
        return true;
    }

    /**
     * Tokenizes the given book record.
     *
     * @param bookRecord the book record to tokenize
     * @return an array of tokens
     */
    static String[] tokenizeBookRecord(String bookRecord) {
        String[] tokens = bookRecord.split("\\s*,\\s*(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        // remove double quotes from the tokens
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].replaceAll("^\"|\"$", "");
        }
        return tokens;
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

                    String title = splitBookRecord[0];
                    String author = splitBookRecord[1];
                    double price = Double.parseDouble(splitBookRecord[2]);
                    String isbn = splitBookRecord[3];
                    String genre = splitBookRecord[4];
                    int year = Integer.parseInt(splitBookRecord[5]);

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