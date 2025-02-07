package com.ex.library.repository;

import com.ex.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    Book findOneById(String id);

    boolean existsByName(String name);

    boolean existsByAuthor(String author);

    boolean existsByPublicationDate(String publicationDate);

    Page<Book> findAll(Specification<Object> spec, Pageable pageable);
}
