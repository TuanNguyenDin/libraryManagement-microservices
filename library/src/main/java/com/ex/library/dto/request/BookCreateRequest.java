package com.ex.library.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookCreateRequest {

    private String id;
    private String name;
    private String author;
    private String publisher;
    private Date publicationDate;
    private String genre;
    private String description;
    private String coverImageUrl;
    private int quantity;
    private int availableQuantity;
    private Date createdAt;
    private Date updatedAt;

}
