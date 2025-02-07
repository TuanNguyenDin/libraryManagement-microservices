package com.ex.library.config;

import com.ex.library.entity.Users;
import com.ex.library.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InitConfig {
    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    ApplicationRunner applicationRunner(UserRepository repository) {
        return args -> {
            if (repository.findByName(ADMIN_USER_NAME).isEmpty()) {
                Users users = Users.builder()
                        .name(ADMIN_USER_NAME)
                        .password(BCrypt.hashpw(ADMIN_PASSWORD, BCrypt.gensalt()))
                        .email("admin@example.com")
                        .isAdmin(true)
                        .isActive(true)
                        .build();

                repository.save(users);
                log.warn("admin user has been created with default password");
            }
        };
    }
}
