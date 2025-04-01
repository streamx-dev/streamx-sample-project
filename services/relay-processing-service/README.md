# Relay Processing Service

Relay messages between corresponding incoming and outgoing channel.
It's useful for transferring messages between topics if no change is needed, for example when providing delivery service ingested data.

To read Processing Service reference visit https://www.streamx.dev/guides/delivery-service-reference.html.

## Configuration
### Schema Provider

Relay Processing Service supports AVRO schema generation for Pulsar channel.

Define schema for incoming and outgoing channel, by setting environment variables:

```
MP_MESSAGING_INCOMING_MESSAGES_SCHEMA="page-schema"
MP_MESSAGING_INCOMING_RELAYED-MESSAGES_SCHEMA="page-schema"
```

You can choose from:
- `page-schema`
- `product-schema`
- `web-resource-schema`

You can add your own schema to service, by adding:

```java
  @Produces
  @Identifier("your-class-schema")
  Schema<YourClass> yourClassSchema = Schema.AVRO(YourClass.class);
```

and setting `your-class-schema` in environment variables.

### Channels

Incoming channels:
- `messages`

Outgoing channels: 
- `relayed-messages`
