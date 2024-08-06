package com.coinone.kafka.connect.bigquery;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class BigQuerySinkConfig extends AbstractConfig {
    public static final ConfigDef CONFIG = new ConfigDef();

    public BigQuerySinkConfig(Map<String, String> originals) {
        super(CONFIG, originals, false);
    }

}
