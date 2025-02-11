package com.example.book_services.entity.dto.request;

import jakarta.validation.constraints.Future;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRequest {

    private String memberId;
    private String bookId;
    private Date borrowDate;
    @Future
    private Date returnDate;
    private boolean isReturned = false;
}
