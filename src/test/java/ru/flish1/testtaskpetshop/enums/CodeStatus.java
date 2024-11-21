package ru.flish1.testtaskpetshop.enums;

public enum CodeStatus {
    SUCCESS(200),
    NOT_FOUND(404),
    INVALID_ID(400),
    NO_DATA(405),
    UNSUPPORTED_MEDIA_TYPE(415);
    private int code;

    CodeStatus(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
