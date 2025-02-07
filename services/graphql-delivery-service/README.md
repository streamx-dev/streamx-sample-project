# GraphQL Delivery Service

This project is sample implementation of Delivery Service, which exposes GraphQL API of provided `products`.

It exposes the following endpoints:
* [/q/graphql-ui/](http://localhost:8084/q/graphql-ui/) - sample GraphQL console
* [/graphql](http://localhost:8084/graphql) - endpoint allowing making request to GraphQL
* [/graphql/schema.graphql](http://localhost:8084/graphql/schema.graphql) - endpoint exposing the complete schema of the GraphQL API

## Supported GraphQL Queries:
* allProducts - returns `product` by specified `key`
```graphql
query {
    allProducts {
        name
        description
        imageUrl
    }
}
```
* getProduct - returns `product` by specified `key`
```graphql
query {
    getProduct(key:"spray-bottle") {
        name
        description
        imageUrl
    }
}
```

## Configuration
### Channels

Incoming channels:
- `products` 

### Example environment variables config

```
MP_MESSAGING_INCOMING_PRODUCTS_TOPIC: "persistent://streamx/outboxes/products"
```
