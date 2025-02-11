package com.example.book_services.mapper;

import com.example.book_services.entity.Book;
import com.example.book_services.entity.dto.request.BookCreateRequest;
import com.example.book_services.entity.dto.response.BookResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {
    Book toBook(BookCreateRequest request);

    Book toBook(BookResponse request);

    Book updateBook(@MappingTarget Book book, BookCreateRequest request);
}
