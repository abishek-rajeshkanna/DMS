package com.hyundai.DMS.exception;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String message) { super(message); }
}
