package io.novalytics.snow;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class SnowSinkConfig extends AbstractConfig {
    public static final String INSTANCE_URL = "snow.instance.url";
    public static final String USERNAME = "snow.username";
    public static final String PASSWORD = "snow.password";
    public static final String TABLE_NAME = "snow.table.name";

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
        .define(INSTANCE_URL, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "ServiceNow instance URL")
        .define(USERNAME, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "ServiceNow user")
        .define(PASSWORD, ConfigDef.Type.PASSWORD, ConfigDef.Importance.HIGH, "ServiceNow password")
        .define(TABLE_NAME, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Target ServiceNow table");

    public SnowSinkConfig(Map<String, String> originals) {
        super(CONFIG_DEF, originals);
    }
}
