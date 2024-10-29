package com.coinone.kafka.connect.bigquery.exception;

public class BigQueryClientException extends BigQueryConnectorException {
    public BigQueryClientException(String message, Throwable cause) {
        super(message, cause);
    }
} 