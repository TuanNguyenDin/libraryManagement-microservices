package com.ex.library.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private boolean isAdmin;
    private boolean isActive;
    private boolean isBanned;
    private Date createdAt;
    private Date updateAt;
}
