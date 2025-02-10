# Json Converter Processing Service

Converts products to JSON-serialized entities.

## Configuration
### Channels

Incoming channels:
- `products`

Outgoing channels: 
- `jsons`

### Example environment variables config
```
MP_MESSAGING_INCOMING_PRODUCTS_TOPIC: "persistent://streamx/inboxes/products"
MP_MESSAGING_OUTGOING_JSONS_TOPIC: "persistent://streamx/outboxes/jsons"
```
