package com.ex.library.controller;

import com.ex.library.dto.request.BorrowRequest;
import com.ex.library.dto.response.APIResponse;
import com.ex.library.dto.request.BookCreateRequest;
import com.ex.library.entity.Book;
import com.ex.library.service.BookService;
import com.ex.library.service.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/book")
public class BookController {
    @Autowired
    private BookService service;
    @Autowired
    private CSVReader readFile;

    @GetMapping()
    public APIResponse<Page<Book>> searchBooks() {
        return APIResponse.<Page<Book>>builder()
                .response(service.searchBooks("", "", "", 0, 10))
                .build();
    }

    @GetMapping("search/{id}")
    public APIResponse<Book> getBook(@PathVariable String id) {
        return service.findBookById(id);
    }

    @PostMapping("search")
    public APIResponse<Page<Book>> searchBooks(@RequestBody BookCreateRequest request) {
        return APIResponse.<Page<Book>>builder()
                .response(
                        service.searchBooks(
                                request.getName(),
                                request.getAuthor(),
                                request.getGenre(),
                                0, 10)
                )
                .build();
    }

    @PostMapping()
    public APIResponse<Book> createNewBook(@RequestBody BookCreateRequest book) {
        return service.createNewBook(book);
    }

    @PutMapping("/{id}")
    public APIResponse<Book> updateBook(@PathVariable String id, @RequestBody BookCreateRequest book) {
        return service.updateBook(id, book);
    }

    @PostMapping("/borrow")
    public APIResponse<Book> borrowBook(@RequestBody BorrowRequest request) {
        return service.borrowABook(request);
    }

    @PostMapping("/upload")
    public APIResponse<?> uploadCSV(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return APIResponse.builder()
                    .message("File CSV trống!")
                    .build();
        }

        try {
            List<Book> books = readFile.CSVToBook(file);
            service.addBooksFormList(books);

            log.info(books.size() + " books loaded");
            return APIResponse.builder()
                    .message("ok")
                    .response(books)
                    .build();
        } catch (Exception e) {
            return APIResponse.builder()
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }
}
