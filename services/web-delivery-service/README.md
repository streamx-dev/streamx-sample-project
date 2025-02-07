# Web Delivery Service

This sample delivery service has one goal - to expose static files on HTTP port.

However, it exposes different types of files:

- files ingested via `pages` channel
- files embedded into delivery service (css and images in [resources](./src/main/resources/META-INF/resources))

## Configuration

- `example.resources.directory` - web storage location, default: `/tmp/streamx`

### Channels

Incoming channels:
- `pages` 

### Example environment variables config

```
MP_MESSAGING_INCOMING_PAGES_TOPIC: "persistent://streamx/outboxes/pages"
```
