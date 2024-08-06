package com.coinone.kafka.connect.bigquery;

import com.coinone.kafka.connect.Versions;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.List;
import java.util.Map;

public class BigQuerySinkConnector extends SinkConnector {
    @Override
    public void start(Map<String, String> map) {

    }

    @Override
    public Class<? extends Task> taskClass() {
        return BigQuerySinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int i) {
        return List.of();
    }

    @Override
    public void stop() {

    }

    @Override
    public ConfigDef config() {
        return null;
    }

    @Override
    public String version() {
        return Versions.VERSION;
    }
}
