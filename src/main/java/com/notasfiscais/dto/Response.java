package com.notasfiscais.dto;

public class Response<T> {

    private T data;
    private String message;

    public Response() {}

    public Response(T data) {
        this.data = data;
    }

    public Response(String message) {
        this.message = message;
    }

    public Response(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
