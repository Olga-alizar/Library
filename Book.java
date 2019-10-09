
//Создаем книгу. Прописываем характеристики (поля, параметры)для книги.
import java.util.Arrays;

class Book {
        private final String title;
        private final String author;
        private final String genre;
        private final String ISBN;
        private final int publishYear;

        //Book() {
        //this("Java  \nМетоды программирования", "И.Н. Блинов, \nВ.С. Романчик", "Учебно-методическое пособие", "978-985-7058-30-3",2013);
        //}

        Book(String bookName, String bookAuthor, String bookGenre, String booknumber, int year){
            title = bookName;
            author = bookAuthor;
            genre = bookGenre;
            ISBN = booknumber;
            publishYear = year;
        }

        @Override
        public String toString () {
            return "Book [title=" + title + ", author=" + author + ", genre=" + genre + ", ISBN=" + ISBN + ", publishYear=" + publishYear + ", ]";
        }
    }



