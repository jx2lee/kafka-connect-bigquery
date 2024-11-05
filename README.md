## Kafka Connect BigQuery Sink Connector
This repository contains a custom Kafka Connect Sink Connector designed to export data from Kafka topics to [Google BigQuery](https://cloud.google.com/bigquery).

The connector is enhanced to support exactly-once semantics using the [BigQuery Storage API](https://cloud.google.com/bigquery/docs/reference/storage), in addition to the default at-least-once delivery.

## Features
- Supports both at-least-once and exactly-once delivery semantics.
- Configurable to use either JSON key files or application default credentials for authentication.
- Easy integration with Kafka Connect framework.

## Getting Started
To get started with the Kafka Connect BigQuery Sink Connector, follow the steps below.

### Prerequisites
- Apache Kafka (version 2.6 or higher)
- Kafka Connect
- Google Cloud account with BigQuery enabled
- Java 17 or higher

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/kafka-connect-bigquery.git
   cd kafka-connect-bigquery
   ```

2. Build the connector:
   ```bash
   ./gradlew build
   ```

3. Copy the built JAR file from `build/libs` to your Kafka Connect's `plugins` directory.

### Configuration
To configure the connector, you need to create a properties file (e.g., `bigquery-sink.properties`) with the following content:

```properties
name=bigquery-sink
connector.class=com.coinone.kafka.connect.bigquery.BigQuerySinkConnector
tasks.max=1
topics=your-kafka-topic
project.id=your-gcp-project-id
key.source=JSON
key=your-json-key-or-file-path
use.storage.api=true
```

- `name`: The name of the connector.
- `connector.class`: The class name of the connector.
- `tasks.max`: Maximum number of tasks to run in parallel.
- `topics`: The Kafka topic(s) to read from.
- `project.id`: Your Google Cloud project ID.
- `key.source`: The source of the authentication key (e.g., JSON).
- `key`: The path to your JSON key file or the key itself.
- `use.storage.api`: Set to `true` to use the BigQuery Storage API for data insertion.

### Exactly-Once Semantics
To enable exactly-once semantics, ensure `delivery.guarantee=exactly_once` is set in your configuration. The connector will use the BigQuery Storage API to achieve this.

## Contributing
Welcome contributions! Please fork the repository and submit pull requests with your improvements.

## License
This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.

## Contact
For any questions or support, please open an issue in the GitHub repository or contact the maintainer [dev.jaejun.lee.1991@gmail.com](mailto:dev.jaejun.lee.1991@gmail.com?subject=Test)
