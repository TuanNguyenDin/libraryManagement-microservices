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
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "code")
    private String code;
    @Future
    @Column(name = "expiry_date")
    private Date expiryDate;
    @Email
    @Column(name = "old_email")
    private String oldEmail;
    @Email
    @Column(name = "new_email")
    private String newEmail;

    @ManyToOne
    @JoinColumn(name = "users.id", nullable = false)
    private Users users;

}
