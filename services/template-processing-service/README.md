# Template Processing Service

Fills predefined templates with product data. As a result it produces two pages:
 - `products.html` which contains general product's data
 - `products/${product.key}.html` - which contains detailed information about each product

This sample service contains usage of `Store` in [ListingCreator](./src/main/java/com/example/project/template/ListingCreator.java). 
Mocking `Store` is implemented in [ListingCreatorTest](./src/test/java/com/example/project/template/ListingCreatorTest.java).

To read Processing Service reference visit https://www.streamx.dev/guides/delivery-service-reference.html.

## Configuration
### Channels

Incoming channels:
- `products`

Outgoing channels: 
- `pages`
