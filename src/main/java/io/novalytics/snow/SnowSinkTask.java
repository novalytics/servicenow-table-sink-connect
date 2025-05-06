package io.novalytics.snow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.kafka.connect.errors.ConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Base64;

public class SnowSinkTask extends SinkTask {
    private static final Logger log = LoggerFactory.getLogger(SnowSinkTask.class);
    private SnowSinkConfig config;
    private ObjectMapper mapper;

    @Override
    public void start(Map<String, String> props) {
        this.config = new SnowSinkConfig(props);
        this.mapper = new ObjectMapper();
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            for (SinkRecord record : records) {
                try {
                    JsonNode json = mapper.readTree(record.value().toString());

                    Map<String, String> payload = new HashMap<>();
                    payload.put("short_description", json.path("message").asText());
                    payload.put("caller_id", json.path("user").asText());
                    payload.put("severity", json.path("level").asText());

                    HttpPost post = new HttpPost(config.getString(SnowSinkConfig.INSTANCE_URL) +
                            "/api/now/table/" + config.getString(SnowSinkConfig.TABLE_NAME));
                    post.setHeader("Content-Type", "application/json");
                    post.setHeader("Accept", "application/json");
                    post.setHeader("Authorization", basicAuth(
                        config.getString(SnowSinkConfig.USERNAME),
                        config.getPassword(SnowSinkConfig.PASSWORD).value()
                    ));

                    StringEntity entity = new StringEntity(mapper.writeValueAsString(payload));
                    post.setEntity(entity);
                    client.execute(post).close();

                } catch (Exception e) {
                    log.error("Error sending record to ServiceNow", e);
                }
            }
        } catch (Exception e) {
            throw new ConnectException("Failed to create HTTP client", e);
        }
    }

    private String basicAuth(String user, String pass) {
        return "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
    }

    @Override
    public void stop() {}

    @Override
    public String version() {
        return "1.0.0";
    }
}
