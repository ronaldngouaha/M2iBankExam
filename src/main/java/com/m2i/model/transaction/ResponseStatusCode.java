package com.m2i.model.transaction;

public interface ResponseStatusCode {
    int SUCCESS = 200;
    int ACCEPTED = 202;
    int BAD_REQUEST = 400;
    int UNAUTHORIZED = 401;
    int FORBIDDEN = 403;
    int NOT_FOUND = 404;
    int INTERNAL_SERVER_ERROR = 500;
    int ACCOUNT_NOT_FOUND = 1001;
    int ACCOUNT_LOCKED = 1002;
    int INSUFFICIENT_FUNDS = 1003;
    int INVALID_AMOUNT = 1004;
    int TRANSACTION_FAILED = 1005;

}
