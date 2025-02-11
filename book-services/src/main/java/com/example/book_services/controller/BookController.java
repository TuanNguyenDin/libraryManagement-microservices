package com.example.book_services.controller;

import com.example.book_services.entity.Book;
import com.example.book_services.entity.dto.request.BookCreateRequest;
import com.example.book_services.services.BookCRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RestController
@RequestMapping("/books")
public class BookController {
    @Autowired
    BookCRUDService bookCRUDService;

    @GetMapping()
    public Page<Book> getBookController() {
        return bookCRUDService.findAllBooks();
    }

    @PostMapping()
    public Book createBook(BookCreateRequest book) {
        return bookCRUDService.createBook(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@RequestBody BookCreateRequest book, @PathVariable String id) {
        return bookCRUDService.updateBook(id, book);
    }

    @DeleteMapping("/{id}")
    public Book deleteBook(@RequestBody String id) {
        return bookCRUDService.deleteBook(id);
    }

    @PostMapping("/upload")
    public Page<Book> uploadFileCSV(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "File cannot be empty");
        return bookCRUDService.uploadFileCSV(file);
    }
}
