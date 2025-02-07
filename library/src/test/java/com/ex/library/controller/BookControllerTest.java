package com.ex.library.controller;

import com.ex.library.dto.request.BookCreateRequest;
import com.ex.library.dto.response.APIResponse;
import com.ex.library.dto.response.BookResponse;
import com.ex.library.entity.Book;
import com.ex.library.mapper.BookMapper;
import com.ex.library.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Nested
@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private BookMapper mapper;
    @MockBean
    private BookService bookService;

    private BookCreateRequest bookCreateRequest;
    private BookResponse bookResponse;

    @BeforeEach
    void setUp() throws Exception {
        bookCreateRequest = BookCreateRequest.builder()
                .name("BookTest")
                .id("1234567")
                .author("John")
                .genre("Test")
                .publisher("Test")
                .publicationDate(new Date(2020, 2, 1))
                .coverImageUrl("http://Test.com")
                .quantity(10)
                .availableQuantity(1)
                .description("Test description")
                .build();

        bookResponse = BookResponse.builder()
                .name("BookTest")
                .author("John")
                .genre("Test")
                .publisher("Test")
                .publicationDate(new Date(2020, 2, 1))
                .coverImageUrl("http://Test.com")
                .description("Test description")
                .build();
    }

    @Test
    public void searchBooks_NoParams() throws Exception {

        List<Book> expectedBooks = Arrays.asList(mapper.toBook(bookCreateRequest), mapper.toBook(bookCreateRequest), mapper.toBook(bookCreateRequest));
        Page<Book> expectedPage = new PageImpl<>(expectedBooks);

        Mockito.when(bookService.searchBooks("", "", "", 0, 10))
                .thenReturn(expectedPage);

        mvc.perform(get("/api/book")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.content").isArray())
                .andExpect(jsonPath("$.response.totalElements").value(expectedPage.getTotalElements()))
                .andExpect(jsonPath("$.response.totalPages").value(expectedPage.getTotalPages()))
                .andExpect(jsonPath("$.response.size").value(expectedPage.getSize()))
                .andExpect(jsonPath("$.response.number").value(expectedPage.getNumber()))
                .andExpect(jsonPath("$.response.last").value(expectedPage.isLast()))
                .andExpect(jsonPath("$.response.first").value(expectedPage.isFirst()));
    }

    @Test
    @WithMockUser
    public void getBook_NonExistentId_ReturnsNotFound() throws Exception {
        String id = "nonexistent-id";

        Mockito.when(bookService.findBookById(ArgumentMatchers.eq(id))).thenThrow(
                new HttpClientErrorException(HttpStatus.NOT_FOUND)
        );

        mvc.perform(get("/api/book/search/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void getBook_ExistentId() throws Exception {
        String id = "nonexistent-id";

        Mockito.when(bookService.findBookById(ArgumentMatchers.eq(id))).thenReturn(
                APIResponse.<Book>builder()
                        .response(mapper.toBook(bookResponse))
                        .build()
        );

        mvc.perform(get("/api/book/search/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.name").value("BookTest"));
    }

    @Test
    public void searchBooks_WithAllParams_ReturnsCorrectResponse() throws Exception {
        List<Book> expectedBooks = Arrays.asList(mapper.toBook(bookCreateRequest), mapper.toBook(bookCreateRequest), mapper.toBook(bookCreateRequest));
        Page<Book> expectedPage = new PageImpl<>(expectedBooks);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(bookCreateRequest);

        Mockito.when(bookService.searchBooks(
                        ArgumentMatchers.eq("BookTest"),
                        ArgumentMatchers.eq("John"),
                        ArgumentMatchers.eq("Test"),
                        ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt()))
                .thenReturn(expectedPage);

        mvc.perform(post("/api/book/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.size").value(expectedPage.getSize()))
                .andExpect(jsonPath("$.response.content").isArray());
    }

    @Test
    public void searchBooks_WithNameParam_ReturnsCorrectResponse() throws Exception {

        String name = "BookTest";
        BookCreateRequest request = BookCreateRequest.builder().name(name).build();
        List<Book> expectedBooks = Arrays.asList(mapper.toBook(bookCreateRequest), mapper.toBook(bookCreateRequest), mapper.toBook(bookCreateRequest));
        Page<Book> expectedPage = new PageImpl<>(expectedBooks);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        Mockito.when(bookService.searchBooks(
                        ArgumentMatchers.eq(name),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt()))
                .thenReturn(expectedPage);

        mvc.perform(post("/api/book/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.size").value(expectedPage.getSize()))
                .andExpect(jsonPath("$.response.content").isArray());
    }

    @Test
    public void searchBooks_WithAuthorParam_ReturnsCorrectResponse() throws Exception {

        String author = "John";
        BookCreateRequest request = BookCreateRequest.builder().author(author).build();
        List<Book> expectedBooks = Arrays.asList(mapper.toBook(bookCreateRequest), mapper.toBook(bookCreateRequest), mapper.toBook(bookCreateRequest));
        Page<Book> expectedPage = new PageImpl<>(expectedBooks);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        Mockito.when(bookService.searchBooks(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(author),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt()))
                .thenReturn(expectedPage);

        mvc.perform(post("/api/book/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.size").value(expectedPage.getSize()))
                .andExpect(jsonPath("$.response.content").isArray());
    }

    @Test
    public void searchBooks_WithGenreParam_ReturnsCorrectResponse() throws Exception {

        String genre = "Test";
        BookCreateRequest request = BookCreateRequest.builder().genre(genre).build();
        List<Book> expectedBooks = Arrays.asList(mapper.toBook(bookCreateRequest), mapper.toBook(bookCreateRequest), mapper.toBook(bookCreateRequest));
        Page<Book> expectedPage = new PageImpl<>(expectedBooks);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        Mockito.when(bookService.searchBooks(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(genre),
                        ArgumentMatchers.anyInt(),
                        ArgumentMatchers.anyInt()))
                .thenReturn(expectedPage);

        mvc.perform(post("/api/book/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.size").value(expectedPage.getSize()))
                .andExpect(jsonPath("$.response.content").isArray());
    }

    @Test
    public void updateBook_NonExistentId_ReturnsNotFound() throws Exception {
        String id = "nonexistent-id";
        BookCreateRequest bookCreateRequest = new BookCreateRequest();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(bookCreateRequest);

        Mockito.when(bookService.updateBook(ArgumentMatchers.eq(id), ArgumentMatchers.any(BookCreateRequest.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        mvc.perform(put("/api/book/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateBook_InvalidRequestBody_ReturnsBadRequest() throws Exception {
        String id = "valid-id";
        BookCreateRequest invalidBookCreateRequest = BookCreateRequest.builder()
                .name("")
                .id("1234567")
                .author("John")
                .genre("Test")
                .publisher("Test")
                .publicationDate(new Date(2020, 2, 1))
                .coverImageUrl("http://Test.com")
                .quantity(10)
                .availableQuantity(1)
                .description("Test description")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(invalidBookCreateRequest);

        Mockito.when(bookService.updateBook(ArgumentMatchers.eq(id), ArgumentMatchers.any(BookCreateRequest.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        mvc.perform(put("/api/book/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}