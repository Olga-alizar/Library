#12.10.2019 Alizarchik O.
create database library;
use library;
CREATE TABLE genre (
    genreName VARCHAR(20) NOT NULL PRIMARY KEY
 );
 CREATE TABLE author  
(
id INT PRIMARY KEY AUTO_INCREMENT, 
authorName varchar(50) NOT NULL,  
birthDate DATE
);
CREATE TABLE book 
(
id INT PRIMARY KEY AUTO_INCREMENT, 
bookName varchar(50) NOT NULL, 
author_id INT,
genre varchar(20), 
ISBN varchar(20),  
printDate DATE,
   FOREIGN KEY (author_id)
        REFERENCES author (id)
        ON DELETE CASCADE
) ;
alter table book add index par_ind(author_id);

