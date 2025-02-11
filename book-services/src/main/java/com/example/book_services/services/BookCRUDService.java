package com.example.book_services.services;

import com.example.book_services.entity.Book;
import com.example.book_services.entity.dto.request.BookCreateRequest;
import com.example.book_services.entity.specifications.BookSpecifications;
import com.example.book_services.mapper.BookMapper;
import com.example.book_services.repo.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
public class BookCRUDService {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BookMapper mapper;

    @Autowired
    CSVReader readFileCSV;

    @Transactional("transactionManager")
    public Book createBook(BookCreateRequest book) {
        Book create = mapper.toBook(book);
        if (bookRepository.existsByName(create.getName())
                && bookRepository.existsByAuthor(create.getAuthor())
                && bookRepository.existsByPublicationDate(create.getPublicationDate())) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Book already exists");
        }
        return bookRepository.save(create);
    }

    @Transactional("transactionManager")
    public Book updateBook(String id, BookCreateRequest book) {
        Book update = mapper.toBook(book);
        return bookRepository.save(update);
    }

    @Transactional("transactionManager")
    public Book deleteBook(String bookId) {
        Book book = bookRepository.findOneById(bookId);
        if (!bookRepository.existsByName(book.getName())
                && !bookRepository.existsByAuthor(book.getAuthor())
                && !bookRepository.existsByPublicationDate(book.getPublicationDate())) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Book not exists in system");
        } else {
            bookRepository.deleteById(bookId);
        }
        return book;
    }

    public Page<Book> findAllBooks() {
        return searchBooks(null, null, null);
    }

    public Page<Book> searchBooks(String title, String author, String category) {
        Specification<Object> spec = Specification.where(BookSpecifications.hasName(title))
                .and(BookSpecifications.hasAuthor(author))
                .and(BookSpecifications.hasGenre(category));
        Pageable pageable = PageRequest.of(0, 10);
        return bookRepository.findAll(spec, pageable);
    }

    @Transactional("transactionManager")
    public Page<Book> uploadFileCSV(MultipartFile file) {
        List<Book> books = readFileCSV.CSVToBook(file);
        List<Book> result = new java.util.ArrayList<>(books);
        books.forEach(book -> {
            if (bookRepository.existsByName(book.getName())
                    && bookRepository.existsByAuthor(book.getAuthor())
                    && bookRepository.existsByPublicationDate(String.valueOf(book.getPublicationDate()))) {
                result.remove(book);
            }
        });

        if (!result.isEmpty()) {
            bookRepository.saveAll(result);
        } else {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Duplicate books");
        }
        Pageable pageable = PageRequest.of(0, 10);
        return bookRepository.findAll(pageable);
    }
}
