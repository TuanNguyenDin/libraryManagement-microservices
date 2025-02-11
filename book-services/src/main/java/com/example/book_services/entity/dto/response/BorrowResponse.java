package com.example.book_services.entity.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowResponse {

    private String UserId;
    private String BookId;
    private Date borrowDate;
    private Date returnDate;

}
