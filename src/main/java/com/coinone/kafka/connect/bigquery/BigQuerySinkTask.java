package com.coinone.kafka.connect.bigquery;

import com.coinone.kafka.connect.Versions;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.util.Collection;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigQuerySinkTask extends SinkTask {
    static final Logger LOGGER = LoggerFactory.getLogger(BigQuerySinkTask.class);
    private static final String CONNECTOR_TYPE = "sink";

    @Override
    public String version() {
        return Versions.VERSION;
    }

    @Override
    public void start(Map<String, String> map) {
        LOGGER.info("Starting BigQuery sink task");
    }

    @Override
    public void put(Collection<SinkRecord> collection) {

    }

    @Override
    public void stop() {
        LOGGER.info("Stopping BigQuery sink task");
    }
}
