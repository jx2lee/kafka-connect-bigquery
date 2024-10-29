package com.coinone.kafka.connect.bigquery.exception;

public class BigQueryConnectorException extends RuntimeException {
    public BigQueryConnectorException(String message) {
        super(message);
    }

    public BigQueryConnectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public BigQueryConnectorException(Throwable cause) {
        super(cause);
    }
} 