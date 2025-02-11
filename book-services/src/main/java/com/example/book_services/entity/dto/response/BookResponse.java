package com.example.book_services.entity.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookResponse {

    private String name;
    private String author;
    private String publisher;
    private Date publicationDate;
    private String genre;
    private String description;
    private String coverImageUrl;

}
