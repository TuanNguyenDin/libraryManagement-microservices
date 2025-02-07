package com.ex.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
    private boolean isAdmin;
    private boolean isActive = false;
    private boolean isBanned = false;
    private Date createdAt;
    private Date updateAt;
}
