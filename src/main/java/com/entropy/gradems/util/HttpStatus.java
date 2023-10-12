package com.entropy.gradems.util;

import lombok.Getter;


@Getter
public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    SEE_OTHER(303, "See Other"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SEVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String description;

    HttpStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
