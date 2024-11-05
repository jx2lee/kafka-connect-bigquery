package com.coinone.kafka.connect.bigquery.exception;

public class BigQueryCredentialsException extends BigQueryConnectorException {
    public BigQueryCredentialsException(String message) {
        super(message);
    }

    public BigQueryCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
} 