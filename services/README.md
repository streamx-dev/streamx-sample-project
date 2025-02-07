# Services

This is the place containing sample services implementing sample business logic.

The current example project contains the following Maven modules:
* [data-model](./data-model/README.md) - contains the model that is shared across all services.
* [relay-processing-service](./relay-processing-service/README.md) - relays messages from one channel to another. 
It's especially useful for providing delivery service ingested messages.
* [template-processing-service](./template-processing-service/README.md) - fills predefined templates with product information.
* [graphql-delivery-service](./graphql-delivery-service/README.md) - allows querying of ingested products.
* [web-delivery-service](./web-delivery-service/README.md) - exposes produced pages and predefined resources.
