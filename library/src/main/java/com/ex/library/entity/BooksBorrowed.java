package com.ex.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BooksBorrowed {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Date borrowDate;
    @Future
    private Date returnDate;
    private boolean isReturned = false;

    @ManyToOne
    @JoinColumn(name = "books.id", nullable = false)
    Book book;
    @ManyToOne
    @JoinColumn(name = "users.id", nullable = false)
    Users users;
}
