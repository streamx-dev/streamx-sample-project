# GraphQL Delivery Service

This project is sample implementation of Delivery Service, which exposes GraphQL API of provided `products`.

It exposes the following endpoints:
* [/q/graphql-ui/](http://localhost:8084/q/graphql-ui/) - sample GraphQL console
* [/graphql](http://localhost:8084/graphql) - endpoint allowing making request to GraphQL
* [/graphql/schema.graphql](http://localhost:8084/graphql/schema.graphql) - endpoint exposing the complete schema of the GraphQL API

To read Delivery Service reference visit https://www.streamx.dev/guides/delivery-service-reference.html.

## Supported GraphQL Queries:
* allProducts - returns `product` by specified `key`
```graphql
query {
    allProducts {
        key
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
        key
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
