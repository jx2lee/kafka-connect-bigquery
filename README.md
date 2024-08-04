## Kafka Connect BigQuery Sink Connector
This repository contains a custom Kafka Connect Sink Connector designed to export data from Kafka topics to [Google BigQuery](https://cloud.google.com/bigquery).

The connector is enhanced to support exactly-once semantics using the [BigQuery Storage API](https://cloud.google.com/bigquery/docs/reference/storage), in addition to the default at-least-once delivery.

## Features
TBD

## Getting Started
TBD
### Prerequisites
TBD
### Installation
TBD
### Configuration
TBD
#### Optional Configuration
TBD
### Running Connector
TBD

### Exactly-Once Semantics
To enable exactly-once semantics, ensure delivery.guarantee=exactly_once is set in your configuration.
The connector will use the BigQuery Storage API to achieve this.


## Contributing
Welcome contributions! Please fork the repository and submit pull requests with your improvements.

## License
This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.

## Contact
For any questions or support, please open an issue in the GitHub repository or contact the maintainer [dev.jaejun.lee.1991\@gmail.com](mailto:dev.jaejun.lee.1991@gmail.com?subject=Test)
