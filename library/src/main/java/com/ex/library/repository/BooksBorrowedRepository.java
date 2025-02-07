package com.ex.library.repository;

import com.ex.library.entity.BooksBorrowed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BooksBorrowedRepository extends JpaRepository<BooksBorrowed, String> {
}
