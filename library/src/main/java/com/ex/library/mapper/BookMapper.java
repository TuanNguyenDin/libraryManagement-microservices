package com.ex.library.mapper;

import com.ex.library.dto.request.BookCreateRequest;
import com.ex.library.dto.response.BookResponse;
import com.ex.library.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {
    Book toBook(BookCreateRequest request);

    Book toBook(BookResponse request);

    Book updateBook(@MappingTarget Book book, BookCreateRequest request);
}
