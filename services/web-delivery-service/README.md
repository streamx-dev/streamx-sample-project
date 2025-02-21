# Web Delivery Service

This sample delivery service has one goal - to expose static files on HTTP port.

However, it exposes different types of files:

- files ingested via `pages` channel
- files embedded into delivery service (css and images in [resources](./src/main/resources/META-INF/resources))
- json-serialized products
- images for products

To read Delivery Service reference visit https://www.streamx.dev/guides/delivery-service-reference.html.

## Configuration

- `example.resources.directory` - web storage location, default: `/tmp/streamx`

### Channels

Incoming channels:
- `pages` 
- `web-resources`
