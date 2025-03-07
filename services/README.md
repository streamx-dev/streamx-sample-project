# Services

This is the place containing sample services implementing sample business logic.

The current example project contains the following Maven modules:
* [data-model](./data-model/README.md) - contains the messages payload model that is shared across all services.
* [relay-processing-service](./relay-processing-service/README.md) - relays messages from one topic to another.
It's useful for transferring messages if no change is needed, for example when providing delivery service ingested data.
* [template-processing-service](./template-processing-service/README.md) - fills predefined templates with product information.
* [graphql-delivery-service](./graphql-delivery-service/README.md) - allows querying of ingested products.
* [web-delivery-service](./web-delivery-service/README.md) - exposes produced pages and resources.
