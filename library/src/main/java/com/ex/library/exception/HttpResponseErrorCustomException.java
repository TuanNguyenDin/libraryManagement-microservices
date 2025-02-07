package com.ex.library.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

@Getter
@Setter
public class HttpResponseErrorCustomException extends HttpClientErrorException {
    private int status;
    private String message;

    public HttpResponseErrorCustomException(int status, String message) {
        super(HttpStatusCode.valueOf(status), message);
        this.status = status;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
