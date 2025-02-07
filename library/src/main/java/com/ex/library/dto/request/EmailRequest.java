package com.ex.library.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailRequest {

    EmailInforRequest sender;
    List<EmailInforRequest> to;
    String subject;
    String htmlContent;
}

