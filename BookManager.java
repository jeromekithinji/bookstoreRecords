import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
        // do_part1();
        // do_part2();
        do_part3();
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
            scanner = new Scanner(
                    new FileInputStream("part1_helper_files" + File.separator + "Part1_input_file_names.txt"));
            int numberOfFiles = scanner.nextInt();
            scanner.nextLine();

            bookRecordsNames = new String[numberOfFiles];
            for (int i = 0; i < numberOfFiles; i++) {
                bookRecordsNames[i] = scanner.nextLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        String[][] genreCounts = { { "CCB", "0" }, { "HCB", "0" }, { "MTV", "0" }, { "MRB", "0" }, { "NEB", "0" },
                { "OTR", "0" }, { "SSM", "0" }, { "TPA", "0" } };
        // read the book record files
        for (int i = 0; i < bookRecordsNames.length; i++) {
            try {
                String errorMessage = "";
                boolean firstError = true;
                scanner = new Scanner(new FileInputStream("part1_helper_files" + File.separator + bookRecordsNames[i]));
                while (scanner.hasNext()) {
                    String bookRecord = scanner.nextLine();
                    String bookRecordTokens[] = tokenizeBookRecord(bookRecord);
                    if (validateBookRecord(bookRecordsNames[i], bookRecordTokens, bookRecord, firstError)) {
                        addRecordToFile(bookRecord, bookRecordTokens[4]);
                        // for (String[] genre : genreCounts) {
                        //     if (bookRecordTokens[4].equals(genre[0])) {
                        //         int count = Integer.parseInt(genre[1]);
                        //         genre[1] = String.valueOf(count + 1);
                        //         break;
                        //     }
                        // }
                    } else {
                        firstError = false;
                    }
                }
                firstError = true;
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            }
        }
        for (String[] genre : genreCounts) {
            System.out.println(genre[0] + " = " + genre[1]);
        }
        scanner.close();
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
                    outputStream.writeInt(validBooks.size());
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

    public static void do_part3() {
        try (Scanner scanner = new Scanner(System.in);) {
            File outputFolder = new File("part2_output_files");
            File[] listOfFiles = outputFolder.listFiles();

            File currentFile = listOfFiles[0];
            ArrayList<Book> currentFileBooks = openBinaryFile(currentFile.getName());

            String userOption = "";

            while (userOption != "x") {
                if (userOption.equals("")) {
                    System.out.println("\n-----------------------------");
                    System.out.println("\tMain Menu");
                    System.out.println("-----------------------------\n");
                    System.out
                            .println("v  View the selected file: " + currentFile.getName() + " ("
                                    + currentFileBooks.size()
                                    + " records)");
                    System.out.println("s  Select a file to view");
                    System.out.println("x  Exit");
                    System.out.print("----------------------------- \n\nEnter Your Choice: ");

                    userOption = scanner.nextLine();
                } else if (userOption.equals("x")) {
                    System.out.println("Closing Program...");
                    break;
                } else if (userOption.equals("s")) {
                    System.out.println("\n------------------------------\n" +
                            "\tFile Sub-Menu\n" +
                            "------------------------------\n");

                    for (int i = 0; i < listOfFiles.length; i++) {
                        System.out.println((i + 1) + " " + listOfFiles[i].getName() + " ("
                                + openBinaryFile(listOfFiles[i].getName()).size() + " records)");
                    }

                    System.out.println(listOfFiles.length + 1 + " Exit");
                    System.out.print("Enter Your Choice: ");
                    userOption = scanner.nextLine();

                    if (Integer.parseInt(userOption) == listOfFiles.length + 1) {
                        System.out.println("\nClosing sub menu...\n");
                        userOption = "";

                        continue;
                    } else if (Integer.parseInt(userOption) > listOfFiles.length || Integer.parseInt(userOption) <= 0) {
                        System.out.println("\nInvalid option. Try again.\n");

                        userOption = "s";
                        continue;
                    } else {
                        currentFile = listOfFiles[Integer.parseInt(userOption) - 1];
                        currentFileBooks = openBinaryFile(currentFile.getName());

                        userOption = "";
                        continue;
                    }
                } else if (userOption.equals("v")) {
                    System.out.println("Viewing: " + currentFile.getName());
                    Book currentBook = currentFileBooks.getFirst();
                    int viewCommand = 0;
                    System.out.print("Enter View Command: ");
                    viewCommand = Integer.parseInt(scanner.nextLine());

                    while (viewCommand != 0) {
                        System.out.println(currentFileBooks.toString());
                        int currentBookIndex = currentFileBooks.indexOf(currentBook);
                        if (viewCommand == 1 || viewCommand == -1) {
                            System.out.println(currentBook);
                        } else if (viewCommand > 0) {
                            for (int i = 0; i < viewCommand; i++) {
                                if (currentBookIndex + i < currentFileBooks.size()) {
                                    System.out.println(currentFileBooks.get(currentBookIndex + i));
                                } else {
                                    System.out.println("EOF has been reached");
                                    break;
                                }
                            }
                            // move current index to last displayed book or last book in the list depending whichever comes first
                            currentBookIndex = Math.min(currentBookIndex + viewCommand - 1, currentFileBooks.size() - 1);
                            currentBook = currentFileBooks.get(currentBookIndex);
                        } else if (viewCommand < 0) {
                            int n = Math.abs(viewCommand) - 1; // calculate |n| - 1
                            int startIndex = Math.max(currentBookIndex - n, 0); // ensures it does not go below 0
                            if (startIndex < currentBookIndex - n) {
                                System.out.println("BOF has been reached");
                            }

                            for (int i = startIndex; i <= currentBookIndex; i++) {
                                System.out.println(currentFileBooks.get(i));
                            }

                            currentBookIndex = startIndex;
                            currentBook = currentFileBooks.get(currentBookIndex);
                        }
                        System.out.print("Enter View Command: ");
                        viewCommand = Integer.parseInt(scanner.nextLine());
                    }
                    userOption = "";
                    continue;
                } else {
                    System.out.println("\nInvalid Option.\n");
                    userOption = "";
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Auxiliary methods
    private static ArrayList<Book> openBinaryFile(String filename) {
        ArrayList<Book> genreBooks = null;

        try (ObjectInputStream outputStream = new ObjectInputStream(
                new FileInputStream("part2_output_files" + File.separator + filename))) {

            int genreRecordCount = outputStream.readInt();
            genreBooks = (ArrayList<Book>) outputStream.readObject();

            return genreBooks;

        } catch (ClassNotFoundException e) {
            System.out.println("Class not found.");
            e.printStackTrace();
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO: handle exception
            System.out.println("Unable to read binary file: " + filename);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("");
            e.printStackTrace();
        }

        return genreBooks;
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
                fileName = "Nostalgia_Eclectic_Books.csv";
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
     * @param error      the error message
     * @param firstError a flag indicating if this is the first error for the file
     * @param fileName   the name of the file containing the error
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
     * @param bookRecordFile   the name of the input file containing the book record
     * @param bookRecordTokens the tokens of the book record
     * @param bookRecord       the book record
     * @param firstError       a flag indicating if this is the first error for the
     *                         file
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
     * @param bookRecord       the book record
     * @param bookRecordFile   the name of the input file containing the book record
     * @param firstError       a flag indicating if this is the first error for the
     *                         file
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
     * @param bookRecord       the book record
     * @param bookRecordFile   the name of the input file containing the book record
     * @param firstError       a flag indicating if this is the first error for the
     *                         file
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
}