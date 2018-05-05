package com.todolist.api.model;

public class Message {
    String status = "";
    String message = "";

    public Message(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public Message() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
