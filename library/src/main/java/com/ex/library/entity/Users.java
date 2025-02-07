package com.ex.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Users {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @NotEmpty
    @Column(name = "name", unique = true)
    private String name;
    @NotBlank
    @Column(name = "password")
    private String password;
    @Email
    @Column(name = "email")
    private String email;
    @Column(name = "is_admin")
    private boolean isAdmin;
    @Column(name = "is_active")
    private boolean isActive = false;
    @Column(name = "is_banned")
    private boolean isBanned = false;
    private Date createdAt;
    private Date updateAt;

}
