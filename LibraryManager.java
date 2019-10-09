import java.util.Arrays;

// КЛАСС LibraryManager
public class LibraryManager {
    private Book[] books;

    public LibraryManager(int size) {
        books = new Book[size];
    }

    public static void main(String[] args) {
        LibraryManager lb = new LibraryManager(10);
        lb.testLibrary();
    }

    boolean addBook(Book book) {
        for (int i = 0; i < books.length; i++) {
            if (books[i] == null) {
                books[i] = book;
                return true;
            }
        }
        return false;
    }

    boolean deleteBook(int index) {
        boolean result = books[index] != null;
        books[index] = null;
        return result;

    }

    Book getBook(int index) {
        return books[index];
    }


    void testLibrary() {
        Book b1=new Book("Name1", "Author1", "Genre1", "Number1", 2013);
        Book b2=new Book("Name2", "Author2", "Genre2", "Number2", 2014);
        Book b3=new Book("Name3", "Author3", "Genre3", "Number3", 2015);
        Book b4=new Book("Name4", "Author4", "Genre4", "Number4", 2016);
        addBook(b1);
        addBook(b2);
        addBook(b3);
        addBook(b4);

        System.out.println("after adding");
        System.out.println(this);
        deleteBook(1);
        deleteBook(4);

        System.out.println("after removing");
        System.out.println(this);

    }

    @Override
    public String toString() {
        return "LibraryManager [books=" + Arrays.toString(books) + "]";
    }
}
