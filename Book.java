package package1;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Класс книга
public class Book {
    private int id;
    private String title;
    public Author author;
    private Genre genre;
    private String ISBN;
    private Date printDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBookName() {
        return title;
    }

    public void setBookName(String bookName) {
        this.title = bookName;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        if ( genre == null) this.genre = Genre.NULL;
        else {
            this.genre = Genre.valueOf(genre);
        }
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public Date getPrintDate() {
        return printDate;
    }

    public void setPrintDate(Date printDate) {
        this.printDate = printDate;
    }
    // добавление книги в базу. Сначала ищем по названию, если не находим, то добавляем. Добавляем и жанр аналогично
    public void addBook (String bookName, Author bookAuthor, Genre bookGenre, String bookISBN, String year)  throws SQLException, ParseException {
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd", new Locale("ru"));
        this.title = bookName;
        this.author = bookAuthor;
        this.genre = bookGenre;
        this.ISBN = bookISBN;
        this.printDate = ft.parse(year);

        SetGenre(this.genre);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String bookingDate = sdf.format(this.author.birthDate);
        int result = 0;
        JavaToMySQL jsql = new JavaToMySQL();
        jsql.connectToServer();
        String query = "select id from book where bookName = '"+this.title+"' AND author_id = "+String.valueOf(this.author.id);
        jsql.execQuery(query, true);
        while (jsql.rs.next()) {
            result = jsql.rs.getInt("id");
        }

        if (result == 0) {
            query = "insert into book (bookName, \n" +
                    "author_id,\n" +
                    "genre, \n" +
                    "ISBN,  \n" +
                    "printDate) values ('" + this.title + "'," + String.valueOf(this.author.id) + ",'" + this.genre + "','" + this.ISBN + "', DATE('"+year+"'))";
            jsql.execQuery(query, false);
        }
        jsql.closeconnectToServer();
    }

    // Удаление книги по названию
    public void deleteBook(String nameBook) throws SQLException {
        JavaToMySQL jsql = new JavaToMySQL();
        jsql.connectToServer();

        String query = "delete from book where bookName = '"+nameBook+"'";
        jsql.execQuery(query, false);
        jsql.closeconnectToServer();
    }

    // Изменение стринговых реквизитов книги
    public void updateBook(String nameBook, String nameField, String NewValues) throws SQLException{
        JavaToMySQL jsql = new JavaToMySQL();
        jsql.connectToServer();

        String query = "update book set "+nameField+" = '" + NewValues +"' where bookName = '"+nameBook+"'";
        jsql.execQuery(query, false);
        jsql.closeconnectToServer();
    }

    // Изменение реквизита даты
    public void updateBook(String nameBook, String nameField, String NewValues, boolean thisData) throws SQLException{
        JavaToMySQL jsql = new JavaToMySQL();
        jsql.connectToServer();

        String query = "update book set "+nameField+" = DATE('"+NewValues+"')" + " where bookName = '"+nameBook+"'";
        jsql.execQuery(query, false);
        jsql.closeconnectToServer();
    }

    //Проверка жанра и запись в таблицу
    private void SetGenre(Genre nameGenre)  throws SQLException{
        JavaToMySQL jsql = new JavaToMySQL();
        jsql.connectToServer();

        String result = "";
        // ищем жанр в таблице
        String query = "select genreName from genre where genreName = '"+nameGenre.toString()+"'";
        jsql.execQuery(query, true);
        while (jsql.rs.next()) {
            result = jsql.rs.getString("genreName");
        }
        // если жанр не найден, то вставляем
        if (result.isEmpty()) {
            query = "insert into genre (genreName) values ('"+nameGenre+"')";
            jsql.execQuery(query, false);
        }
        jsql.closeconnectToServer();
    }
    // Удаление жанра
    public static void deleteGenre(String nameGenre)  throws SQLException{
        JavaToMySQL jsql = new JavaToMySQL();
        jsql.connectToServer();

        String query = "update book set genre = null where genre = '"+nameGenre+"'";
        jsql.execQuery(query, false);
        query = "delete from genre where genreName = '"+nameGenre+"'";
        jsql.execQuery(query, false);
        jsql.closeconnectToServer();
    }
    // Получение книги по названию
    public Book getBook(String nameBook)  throws SQLException{
        JavaToMySQL jsql = new JavaToMySQL();
        jsql.connectToServer();

        jsql.execQuery("select b.id, b.bookName, b.author_id, b.genre, b.ISBN, b.printDate, a.id as a_id, a.authorName, a.birthDate from book b\n" +
                "left join author a on a.id = b.author_id where b.bookName = '"+nameBook+"'", true);
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
        Book b1=new Book();
        while (jsql.rs.next()) {
            b1.setId(jsql.rs.getInt("id"));
            b1.setBookName(jsql.rs.getString("bookName"));
            b1.setISBN(jsql.rs.getString("ISBN"));
            b1.setGenre(jsql.rs.getString("genre"));
            b1.setPrintDate(jsql.rs.getDate("printDate"));
            b1.author = new Author();
            b1.author.setId( jsql.rs.getInt("author_id"));
            b1.author.setName(jsql.rs.getString("authorName"));
            b1.author.setBirthDate( jsql.rs.getDate("birthDate"));
            System.out.println(b1);
        }
        jsql.closeconnectToServer();
        return b1;
    }

    @Override
    public String toString () {
        return "Книга [название=" + title + ", [автор=" + author.authorName +", др автора="+author.birthDate  + "], жанр=" + genre.toString() + ", ISBN=" + ISBN + ", дата публикации=" + printDate + ", ]";
    }

}
