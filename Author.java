package package1;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

// Класс Автор
public class Author {
    Integer id;
    String authorName;
    private Set<Book> books;
    Date birthDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return authorName;
    }

    public void setName(String name) {
        this.authorName = name;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    // метод добаления автора. Сначала ищем в базе по имени, если не находим, то добавляем
    public void addAuthor(String Name, String birthDat) throws ParseException, SQLException  {
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd", new Locale("ru"));
        this.authorName = Name;
        this.birthDate = ft.parse(birthDat);
        //books.add(oBooks);

        int a_id=0, result=0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String birDate = sdf.format(this.birthDate);
        String query = "select id from author where authorName = '"+this.authorName+"'";
        JavaToMySQL jsql = new JavaToMySQL();
        jsql.connectToServer();
        jsql.execQuery(query, true);
        while (jsql.rs.next()) {
            result = jsql.rs.getInt("id");
        }
        if (result > 0){
            a_id = result;
        }else
        {
            query = "SET FOREIGN_KEY_CHECKS = 0";
            jsql.execQuery(query, false);
            query = "insert into author (authorName,  \n" +
                    "birthDate) values ('"+this.authorName+"',DATE('"+birDate+"')"+")";
            jsql.execQuery(query, false);

            query = "select id from author where authorName = '"+this.authorName+"'";
            jsql.execQuery(query, true);
            while (jsql.rs.next()) {
                a_id = jsql.rs.getInt("id");
            }
        }
        this.id = a_id;
        jsql.closeconnectToServer();
    };
    // удаление автора. удаляются также и книги автора
    public void deleteAuthor(String authorName) {
        JavaToMySQL jsql = new JavaToMySQL();
        jsql.connectToServer();
        jsql.execQuery("SET FOREIGN_KEY_CHECKS = 1", false);
        String query = "delete from author where authorName = '"+authorName+"'";
        jsql.execQuery(query,false);
        jsql.closeconnectToServer();
    }

}
