package com.m2i.model.transaction;

public class RequestResponse<T>  {

    public final Integer statusCode;
    public final String responseMessage;
    public final T responseValue;

    public RequestResponse(Integer statusCode, String responseMessage, T responseValue) {
        this.statusCode = statusCode;
        this.responseMessage = responseMessage;
        this.responseValue = responseValue;
    }


    public T getResponseValue() {
        return responseValue;
    }
        public Integer getStatusCode() {
            return statusCode;
        }

    public String getResponseMessage() {
        return responseMessage;
    }


    public String toString() {
        return "RequestResponse{" +
                "statusCode=" + statusCode +
                ", responseMessage='" + responseMessage + '\'' +
                ", responseValue=" + responseValue +
                '}';
    }
}
