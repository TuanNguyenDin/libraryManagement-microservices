package com.example.book_services.entity.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class BookSpecifications {

    public static Specification<Object> hasAuthor(String author) {
        return (root, query, criteriaBuilder) ->
                StringUtils.hasText(author) ? criteriaBuilder.like(root.get("author"), "%" + author + "%") : null;
    }

    public static Specification<Object> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                StringUtils.hasText(name) ? criteriaBuilder.like(root.get("name"), "%" + name + "%") : null;
    }

    public static Specification<Object> hasGenre(String genre) {
        return (root, query, criteriaBuilder) ->
                StringUtils.hasText(genre) ? criteriaBuilder.equal(root.get("genre"), genre) : null;
    }
}
