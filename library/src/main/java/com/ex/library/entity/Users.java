package com.ex.library.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @NotEmpty
    private String name;
    @NotEmpty
    @NotBlank
    private String password;
    @Email
    private String email;
    private boolean isAdmin;
    private boolean isActive = false;
    private boolean isBanned = false;
    private Date createdAt;
    private Date updateAt;

}
