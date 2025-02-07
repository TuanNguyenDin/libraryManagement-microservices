package com.ex.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerifyCodeChangeEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String code;
    @Future
    private Date expiryDate;
    @Email
    private String oldEmail;
    @Email
    private String newEmail;

    @ManyToOne
    @JoinColumn(name = "users.id", nullable = false)
    private Users users;

}
