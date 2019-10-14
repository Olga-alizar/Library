// Итоговая работа О. Ализарчик
// Работа с библиотекой книг
// Минск, 2019
import java.util.*;
import java.text.*;
import java.lang.*;
// классы для работы с MySQL
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// КЛАСС LibraryManager
public class LibraryManager {

    private static ArrayList<BookS> books = new ArrayList<>();
    // JDBC URL, username and password of MySQL server
    private static final String url = "jdbc:mysql://localhost:3306/library";
    private static final String user = "admin";
    private static final String password = "User_mysql0$";

    // JDBC variables for opening and managing connection
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    public static void main(String[] args) throws ParseException{
        LibraryManager lb = new LibraryManager();
        lb.testLibrary();
    }
    // Получаем список всех книг библиотеки
    void readBooks(Statement stmt)  throws SQLException{
        rs = stmt.executeQuery("select b.id, b.bookName, b.author_id, b.genre, b.ISBN, b.printDate, a.id as a_id, a.authorName, a.birthDate from book b\n" +
                "left join author a on a.id = b.author_id");
        books.clear();
        while (rs.next()) {
            BookS b1=new BookS();
            b1.id = rs.getInt("id");
            b1.title = rs.getString("bookName");
            b1.ISBN = rs.getString("ISBN");
            b1.genre = rs.getString("genre");
            b1.printDate = rs.getDate("printDate");
            b1.author = new Author();
            b1.author.id = rs.getInt("author_id");
            b1.author.authorName = rs.getString("authorName");
            b1.author.birthDate = rs.getDate("birthDate");
            books.add(b1);
            System.out.println(b1);
            b1 = null;
        }
    }

    // тестируем библиотеку
    void testLibrary() throws ParseException{
        try {
            // opening database connection to MySQL server
            con = DriverManager.getConnection(url, user, password);

            // getting Statement object to execute query
            stmt = con.createStatement();

            // Заполняем библиотеку
            Author a1=new Author();
            BookS b1=new BookS();

            Genre g1 = Genre.SCIENCE;
            a1.addAuthor("Author1","1953-01-01", stmt);
            b1.addBook("Name1", a1, g1.toString(), "Number1", "2013-01-15", stmt);
            a1.addAuthor("Author2","1963-02-02", stmt);
            b1.addBook("Name2", a1, Genre.BELLETRE.toString(), "Number2", "2014-01-15", stmt);
            a1.addAuthor("Author3","1973-03-03", stmt);
            b1.addBook("Name3", a1, Genre.PHANTASY.toString(), "Number3", "2015-01-15", stmt);
            a1.addAuthor("Author4","1983-04-04", stmt);
            b1.addBook("Name4", a1, Genre.SCIENCE_FICTION.toString(), "Number4", "2016-01-15", stmt);
            a1.addAuthor("Author4","1983-04-04", stmt);
            b1.addBook("Name5", a1, Genre.SCIENCE_FICTION.toString(), "Number5", "2011-01-15", stmt);
            a1.addAuthor("О.Бузова","1999-01-01", stmt);
            b1.addBook("Цена счастья", a1, Genre.PHANTASY.toString(), "I9HB8BBK", "2010-01-01", stmt);
            a1.addAuthor("В.С. Романчик","1959-11-01", stmt);
            b1.addBook("Java. Методы программирования", a1, Genre.Textbook.toString(), "978-985-7058-30-3", "2013-01-01", stmt);

            System.out.println("После добавления книг:");
            readBooks(stmt);

            //Получение книги
            BookS get1=new BookS();
            BookS rb = get1.getBook("Name1", stmt);
            System.out.println("Получена книга:");
            System.out.println(rb.toString());

            BookS db1=new BookS();
            db1.deleteGenre(Genre.SCIENCE_FICTION.toString(), stmt);
            System.out.println("После удаления жанра:");
            readBooks(stmt);
            // удаление книги Name2
            db1.deleteBook("Name2", stmt);
            // замена имени Name3 на Книга3
            db1.updateBook("Name3", "bookName", "Книга3", stmt);
            // выдача книги Книга3
            db1.getBook("Книга3", stmt);
            // замена поля printDate в книге Name4 на 1996-10-25
            db1.updateBook("Name1", "printDate", "1996-10-25",true, stmt);
            Author da1=new Author();
            // удаление автора Author4. также удаляются и книги автора
            da1.deleteAuthor("Author4", stmt);

            System.out.println("После удаления:");
            readBooks(stmt);

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //close connection ,stmt and resultset here
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
            try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        }

    }
}

// Класс книга
class BookS {
    int id;
    String title;
    Author author;
    String genre;
    String ISBN;
    Date printDate;

    // добавление книги в базу. Сначала ищем по названию, если не находим, то добавляем. Добавляем и жанр аналогично
    void addBook (String bookName, Author bookAuthor, String bookGenre, String bookISBN, String year, Statement stmt)  throws SQLException, ParseException {
        ResultSet rs;
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd", new Locale("ru"));
        this.title = bookName;
        this.author = bookAuthor;
        this.genre = bookGenre;
        this.ISBN = bookISBN;
        this.printDate = ft.parse(year);

        SetGenre(this.genre, stmt);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String bookingDate = sdf.format(this.author.birthDate);
        int result = 0;
        String query = "select id from book where bookName = '"+this.title+"' AND author_id = "+String.valueOf(this.author.id);
        rs = stmt.executeQuery(query);
        while (rs.next()) {
            result = rs.getInt("id");
        }

        if (result == 0) {
            query = "insert into book (bookName, \n" +
                    "author_id,\n" +
                    "genre, \n" +
                    "ISBN,  \n" +
                    "printDate) values ('" + this.title + "'," + String.valueOf(this.author.id) + ",'" + this.genre + "','" + this.ISBN + "', DATE('"+year+"'))";
            stmt.execute(query);
        }
    }

    // Удаление книги по названию
    void deleteBook(String nameBook, Statement stmt) throws SQLException {
        String query = "delete from book where bookName = '"+nameBook+"'";
        stmt.execute(query);
    }

    // Изменение стринговых реквизитов книги
    void updateBook(String nameBook, String nameField, String NewValues, Statement stmt) throws SQLException{
        String query = "update book set "+nameField+" = '" + NewValues +"' where bookName = '"+nameBook+"'";
        stmt.execute(query);
    }

    // Изменение реквизита даты
    void updateBook(String nameBook, String nameField, String NewValues, boolean thisData, Statement stmt) throws SQLException{
        String query = "update book set "+nameField+" = DATE('"+NewValues+"')" + " where bookName = '"+nameBook+"'";
        stmt.execute(query);
    }

    //Проверка жанра и запись в таблицу
    void SetGenre(String nameGenre, Statement stmt)  throws SQLException{
        ResultSet rs;
        String result = "";
        // ищем жанр в таблице
        String query = "select genreName from genre where genreName = '"+nameGenre+"'";
        rs = stmt.executeQuery(query);
        while (rs.next()) {
            result = rs.getString("genreName");
        }
        // если жанр не найден, то вставляем
        if (result.isEmpty()) {
            query = "insert into genre (genreName) values ('"+nameGenre+"')";
            stmt.execute(query);
        }
    }
    // Удаление жанра
    void deleteGenre(String nameGenre, Statement stmt)  throws SQLException{
        String query = "update book set genre = null where genre = '"+nameGenre+"'";
        stmt.execute(query);
        query = "delete from genre where genreName = '"+nameGenre+"'";
        stmt.execute(query);
    }
    // Получение книги по названию
    BookS getBook(String nameBook,  Statement stmt)  throws SQLException{
        ResultSet rs;
        rs = stmt.executeQuery("select b.id, b.bookName, b.author_id, b.genre, b.ISBN, b.printDate, a.id as a_id, a.authorName, a.birthDate from book b\n" +
                "left join author a on a.id = b.author_id where b.bookName = '"+nameBook+"'");
        /*
        int i = 0;
        id = 1,
                bookName = "Цена счастья",
                author = {
                        id = 1,
                        name = "О.Бузова",
                        birthDate = 01.01.1999
                },
                genre = "fantasy",
                ISBN = "I9HB8BBK",
                printDate = 01.01.2001
       */
        BookS b1=new BookS();
        while (rs.next()) {
            b1.id = rs.getInt("id");
            b1.title = rs.getString("bookName");
            b1.ISBN = rs.getString("ISBN");
            b1.genre = rs.getString("genre");
            b1.printDate = rs.getDate("printDate");
            b1.author = new Author();
            b1.author.id = rs.getInt("author_id");
            b1.author.authorName = rs.getString("authorName");
            b1.author.birthDate = rs.getDate("birthDate");
            System.out.println(b1);
        }
        return b1;
    }

    @Override
    public String toString () {
        return "Книга [название=" + title + ", [автор=" + author.authorName +", др автора="+author.birthDate  + "], жанр=" + genre + ", ISBN=" + ISBN + ", дата публикации=" + printDate + ", ]";
    }
}
// Класс Автор
class Author{
    Integer id;
    String authorName;
    Set<BookS> books;
    Date birthDate;

    // метод добаления автора. Сначала ищем в базе по имени, если не находим, то добавляем
    void addAuthor(String Name, String birthDat, Statement stmt) throws ParseException, SQLException{
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd", new Locale("ru"));
        this.authorName = Name;
        this.birthDate = ft.parse(birthDat);
        //books.add(oBooks);

        ResultSet rs;
        int a_id=0, result=0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String birDate = sdf.format(this.birthDate);
        String query = "select id from author where authorName = '"+this.authorName+"'";
        rs = stmt.executeQuery(query);
        while (rs.next()) {
            result = rs.getInt("id");
        }
        if (result > 0){
            a_id = result;
        }else
        {
            query = "SET FOREIGN_KEY_CHECKS = 0";
            stmt.execute(query);
            query = "insert into author (authorName,  \n" +
                    "birthDate) values ('"+this.authorName+"',DATE('"+birDate+"')"+")";
            stmt.execute(query);

            query = "select id from author where authorName = '"+this.authorName+"'";
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                a_id = rs.getInt("id");
            }
        }
        this.id = a_id;
    };
    // Удаление автора. Удаляются также и книги автора.
    void deleteAuthor(String authorName, Statement stmt) throws SQLException{
        stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        String query = "delete from author where authorName = '"+authorName+"'";
        stmt.execute(query);
    }
}
// Список жанров
enum Genre
{
    SCIENCE,
    BELLETRE,
    PHANTASY,
    SCIENCE_FICTION,
    Textbook
}