package com.ex.library.entity;

import jakarta.persistence.*;
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
    @Column(name = "id")
    private String id;
    @Column(name = "borrow_date")
    private Date borrowDate;
    @Column(name = "return_date")
    private Date returnDate;
    @Column(name = "is_returned")
    private boolean isReturned = false;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "books.id", nullable = false)
    Book book;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "users.id", nullable = false)
    Users users;
}
