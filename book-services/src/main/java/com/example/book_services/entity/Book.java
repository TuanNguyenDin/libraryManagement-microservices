package com.example.book_services.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private String id;
    @NotEmpty
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "author")
    private String author;
    @Column(name = "publisher")
    private String publisher;
    @Column(name = "publication_date")
    private String publicationDate;
    @Column(name = "genre")
    private String genre;
    @Column(name = "description")
    private String description;
    @Column(name = "cover_image_url")
    private String coverImageUrl;
    @Min(0)
    @Column(name = "quantity")
    private int quantity;
    @Min(0)
    @Column(name = "available_quantity")
    private int availableQuantity;
    private Date createdAt;
    private Date updatedAt;

}
