package com.anthony.notifications_api.exception;

public class InvalidNotificationException
        extends RuntimeException {

    public InvalidNotificationException(String message) {
        super(message);
    }
}