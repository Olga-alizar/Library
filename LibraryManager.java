// Итоговая работа О. Ализарчик
// Работа с библиотекой книг
// Минск, 2019
import java.sql.SQLException;
import java.util.*;
import java.text.*;
import java.lang.*;
import package1.*;

public class LibraryManager {

    protected static ArrayList<Book> books;

    static {
        books = new ArrayList<>();
    }

    public static void main(String[] args) throws ParseException, SQLException{
        LibraryManager lb = new LibraryManager();
        lb.testLibrary();
    }
    // Получаем список всех книг библиотеки
    private void readBooks()  throws SQLException {
        JavaToMySQL jsql = new JavaToMySQL();
        jsql.connectToServer();

        jsql.execQuery("select b.id, b.bookName, b.author_id, b.genre, b.ISBN, b.printDate, a.id as a_id, a.authorName, a.birthDate from book b\n" +
                "left join author a on a.id = b.author_id", true);
        books.clear();
        while (jsql.rs.next()) {
            Book b1=new Book();
            b1.setId(jsql.rs.getInt("id"));
            b1.setBookName( jsql.rs.getString("bookName"));
            b1.setISBN( jsql.rs.getString("ISBN"));
            b1.setGenre( jsql.rs.getString("genre"));
            b1.setPrintDate( jsql.rs.getDate("printDate"));
            b1.author = new Author();
            b1.author.setId( jsql.rs.getInt("author_id"));
            b1.author.setName(jsql.rs.getString("authorName"));
            b1.author.setBirthDate( jsql.rs.getDate("birthDate"));
            books.add(b1);
            System.out.println(b1.toString());
            b1 = null;
        }
        jsql.closeconnectToServer();
    }

    // тестируем библиотеку
    private void testLibrary() throws ParseException, SQLException{
        // Заполняем библиотеку
        Author a1=new Author();
        Book b1=new Book();

        Genre g1 = Genre.SCIENCE;
        a1.addAuthor("Author1","1953-01-01");
        b1.addBook("Name1", a1, g1, "Number1", "2013-01-15");
        a1.addAuthor("Author2","1963-02-02");
        b1.addBook("Name2", a1, Genre.BELLETRE, "Number2", "2014-01-15");
        a1.addAuthor("Author3","1973-03-03");
        b1.addBook("Name3", a1, Genre.PHANTASY, "Number3", "2015-01-15");
        a1.addAuthor("Author4","1983-04-04");
        b1.addBook("Name4", a1, Genre.SCIENCE_FICTION, "Number4", "2016-01-15");
        a1.addAuthor("Author4","1983-04-04");
        b1.addBook("Name5", a1, Genre.SCIENCE_FICTION, "Number5", "2011-01-15");
        a1.addAuthor("О.Бузова","1999-01-01");
        b1.addBook("Цена счастья", a1, Genre.PHANTASY, "I9HB8BBK", "2010-01-01");
        a1.addAuthor("В.С. Романчик","1959-11-01");
        b1.addBook("Java. Методы программирования", a1, Genre.Textbook, "978-985-7058-30-3", "2013-01-01");

        System.out.println("После добавления книг:");
        readBooks();

        //Получение книги
        Book get1=new Book();
        Book rb = get1.getBook("Name1");
        System.out.println("Получена книга:");
        System.out.println(rb.toString());

        Book db1=new Book();
        Book.deleteGenre(Genre.SCIENCE_FICTION.toString());
        System.out.println("После удаления жанра:");
        readBooks();
        // удаление книги Name2
        db1.deleteBook("Name2");
        // замена имени Name3 на Книга3
        db1.updateBook("Name3", "bookName", "Книга3");
        // выдача книги Книга3
        db1.getBook("Книга3");
        // замена поля printDate в книге Name4 на 1996-10-25
        db1.updateBook("Name1", "printDate", "1996-10-25",true);
        Author da1=new Author();
        // удаление автора Author4. также удаляются и книги автора
        da1.deleteAuthor("Author4");

        System.out.println("После удаления:");
        readBooks();
    }

}