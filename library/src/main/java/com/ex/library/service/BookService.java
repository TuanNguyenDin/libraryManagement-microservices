package com.ex.library.service;

import java.util.Date;
import java.util.List;

import com.ex.library.dto.request.BorrowRequest;
import com.ex.library.dto.response.APIResponse;
import com.ex.library.dto.request.BookCreateRequest;
import com.ex.library.entity.Book;
import com.ex.library.entity.BooksBorrowed;
import com.ex.library.exception.CustomException;
import com.ex.library.exception.ErrorCode;
import com.ex.library.mapper.BookMapper;
import com.ex.library.repository.BookRepository;
import com.ex.library.repository.BooksBorrowedRepository;
import com.ex.library.repository.UserRepository;
import com.ex.library.specifications.BookSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BooksBorrowedRepository booksBorrowedRepository;
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public APIResponse<Book> createNewBook(BookCreateRequest bookCreate) {

        APIResponse<Book> apiResponse = new APIResponse<>();

        bookCreate.setCreatedAt(new Date());
        Book book = bookMapper.toBook(bookCreate);

        apiResponse.setResponse(bookRepository.save(book));
        return apiResponse;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public APIResponse<Book> updateBook(String id, BookCreateRequest request) {
        Book book = bookRepository.findOneById(id);
        APIResponse<Book> apiResponse = new APIResponse<>();

        request.setUpdatedAt(new Date());
        Book saveBook = bookMapper.updateBook(book, request);

        apiResponse.setResponse(bookRepository.save(saveBook));
        return apiResponse;
    }

    public APIResponse<List<Book>> findAllBook() {
        APIResponse<List<Book>> apiResponse = new APIResponse<>();
        apiResponse.setResponse(bookRepository.findAll());
        return apiResponse;
    }

    public APIResponse<Book> findBookById(String id) {

        Book book = bookRepository.findOneById(id);
        APIResponse<Book> response = new APIResponse<>();
        if (book == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        response.setResponse(bookRepository.findOneById(id));
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public APIResponse<String> deleteById(String id) {
        APIResponse<String> response = new APIResponse<>();
        if (bookRepository.findOneById(id) != null) {
            bookRepository.deleteById(id);
            response.setMessage("Delete complete");
        } else {
            response.setMessage("Book not found!");
        }
        return response;
    }

    @PreAuthorize("hasRole('USER')")
    public APIResponse<Book> borrowABook(BorrowRequest request) {
        APIResponse<Book> response = new APIResponse<>();
        Book book = bookRepository.findOneById(request.getBookId());

        try {
            if (book.getAvailableQuantity() > 0) {
                BooksBorrowed booksBorrowed = BooksBorrowed.builder()
                        .users(
                                userRepository.findById(request.getMemberId()).orElseThrow(() ->
                                        new CustomException(ErrorCode.USER_NOT_EXISTED))
                        )
                        .book(book)
                        .borrowDate(new java.sql.Date(System.currentTimeMillis()))
                        .returnDate(request.getReturnDate())
                        .build();

                book.setAvailableQuantity(book.getAvailableQuantity() - 1);

                if (booksBorrowed.getReturnDate() == null) {
                    book.setQuantity(book.getQuantity() - 1);
                }        //user buy this book

                bookRepository.save(book);
                booksBorrowedRepository.save(booksBorrowed);
                response.setResponse(book);
            } else {
                response.setMessage("Book is out of stock!");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBooksFormList(List<Book> books) {
        books = books.stream().distinct().toList();         // Remove duplicates based on equals() and hashCode()
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
        } else throw new CustomException(ErrorCode.DUPLICATE_DATA);
    }

    public Page<Book> searchBooks(String title, String author, String category, int page, int size) {
        Specification<Object> spec = Specification.where(BookSpecifications.hasName(title))
                .and(BookSpecifications.hasAuthor(author))
                .and(BookSpecifications.hasGenre(category));
        Pageable pageable = PageRequest.of(page, size);
        log.info("pageable :" + pageable.toString());
        return bookRepository.findAll(spec, pageable);
    }
}
