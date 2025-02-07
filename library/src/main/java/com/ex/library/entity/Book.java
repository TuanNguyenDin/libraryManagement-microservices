package com.ex.library.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private String id;
    @NotEmpty
    private String name;
    private String author;
    private String publisher;
    private String publicationDate;
    private String genre;
    private String description;
    private String coverImageUrl;
    @Min(0)
    private int quantity;
    @Min(0)
    private int availableQuantity;
    private Date createdAt;
    private Date updatedAt;

}
