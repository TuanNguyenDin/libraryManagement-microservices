package com.ex.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Future;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;

import java.sql.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class InvalidateToken {

    @Id
    @Column(name = "id")
    private String id;

    @Future
    @Column(name = "expiry_date")
    private Date expiryDate;
}
