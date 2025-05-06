# ServiceNow Table Sink Connector

A custom Kafka Connect SinkConnector for sending events from a Kafka topic to the ServiceNow Table API, with JSON field mapping and nested field support.

---

## 📦 Features

- ✅ Java 17 & Maven-based project
- ✅ Kafka Connect SinkConnector API (Apache Kafka 3.7.0)
- ✅ Support for nested JSON field mappings using dotted path syntax
- ✅ Only processes records with root `event` object
- ✅ Configurable field mappings via connector config
- ✅ ServiceNow Table API integration via HTTP POST

---

## ⚙️ Installation

1. **Build the project:**

   ```bash
   mvn clean package
   ```

2. **Copy the connector JAR** to your Kafka Connect plugin path:

   ```bash
   cp target/servicenow-table-sink-connect-1.0.0.jar $CONNECT_PLUGIN_PATH/servicenow-sink/
   ```

3. **Create a connector configuration (`connector.properties`):**

   ```properties
   name=servicenow-sink
   connector.class=io.novalytics.snow.SnowSinkConnector
   tasks.max=1
   topics=snow-events

   snow.instance.url=https://yourinstance.service-now.com
   snow.username=your_user
   snow.password=your_password
   snow.table.name=incident
   snow.field.mapping=event.title:short_description,event.related_ci.configuration_item.primary_dns_name:cmdb_ci
   ```

4. **Start the connector:**

   ```bash
   curl -X POST -H "Content-Type: application/json" --data @connector.properties http://localhost:8083/connectors
   ```

---

## 🧠 JSON Field Mapping

Use the `snow.field.mapping` config to define how JSON fields are mapped to ServiceNow fields.

Supports **nested fields** using `.` notation.

**Example:**

```properties
snow.field.mapping=event.title:short_description,event.related_ci.configuration_item.primary_dns_name:cmdb_ci
```

---

## 🛠 Troubleshooting

| Problem | Cause | Solution |
|--------|-------|----------|
| `Skipping non-event record` | JSON does not contain `"event"` root field | Ensure the message starts with an `event` key |
| `Cannot find symbol HttpPost` | Missing HTTP dependencies | Make sure `httpclient5` and `httpcore5` are in your POM |
| Field values are null | Mapping path is incorrect | Double-check `snow.field.mapping` paths match JSON structure |
| Records not arriving in ServiceNow | Auth/API issue | Validate credentials and ServiceNow URL |

---

## 📄 License

MIT or your preferred open source license.
