# Relay Processing Service

Relay messages between corresponding incoming and outgoing channel.

## Configuration
### Channels

Incoming channels:
- `products`

Outgoing channels: 
- `relayed-products`

### Example environment variables config
```
MP_MESSAGING_INCOMING_PRODUCTS_TOPIC: "persistent://streamx/inboxes/products"
MP_MESSAGING_OUTGOING_RELAYED-PRODUCTS_TOPIC: "persistent://streamx/outboxes/relayed-products"
```
