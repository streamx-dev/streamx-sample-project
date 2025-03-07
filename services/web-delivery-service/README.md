# Web Delivery Service

This sample delivery service expose received pages and web resources as static files on HTTP port.
The pages and web resources are updated on every publish action and are removed on unpublish.

It exposes different types of files:

- files ingested via the `pages` channel
- static web resources invested via the `web-resources` channel. This includes:
  - json-serialized products
  - images for products
  - static assets (css and js files)

To read Delivery Service reference visit https://www.streamx.dev/guides/delivery-service-reference.html.

## Configuration

- `example.resources.directory` - web storage location. 
Default: `/tmp/streamx`

### Channels

Incoming channels:
- `pages` 
- `web-resources`
