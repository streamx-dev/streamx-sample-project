#!/usr/bin/env sh

CHANNEL="products"

publishWithCLI() {
# To publish data to Mesh, run
# `streamx publish <channel> <messageKey>`
# and define the payload with -s/-b/-j options.

# For more information, see https://www.streamx.dev/guides/streamx-command-line-interface-reference.html#_streamx_publish
# or run `streamx publish --help`

  KEY=$1
  PRODUCT_NAME=$2
  DESCRIPTION=$3
  IMAGE_URL=$4

  streamx publish $CHANNEL "$KEY" -s "name.string=$PRODUCT_NAME" -s "description.string=$DESCRIPTION" -s "imageUrl.string=$IMAGE_URL"
}

publishWithCurl() {
# Ingestion is also available by sending request directly to Rest Ingestion Service.

  KEY=$1
  PRODUCT_NAME=$2
  DESCRIPTION=$3
  IMAGE_URL=$4

  curl -w " - status: %{response_code} - $KEY publication\n" -X POST "http://localhost:8080/ingestion/v1/channels/$CHANNEL/messages" \
       -H 'Content-Type: application/json' \
       -d "{
             \"key\" : \"$KEY\",
             \"action\" : \"publish\",
             \"eventTime\" : null,
             \"properties\" : { },
             \"payload\" : {
               \"org.example.project.model.Product\" : {
                 \"name\" : {
                   \"string\" : \"$PRODUCT_NAME\"
                 },
                 \"description\" : {
                   \"string\" : \"$DESCRIPTION\"
                 },
                 \"imageUrl\" : {
                   \"string\" : \"$IMAGE_URL\"
                 }
               }
             }
           }"
}

publish() {
  publishWithCLI "$1" "$2" "$3" "$4"
#  publishWithCurl "$1" "$2" "$3" "$4"
}

unpublishWithCLI() {
# Ingestion with StreamX CLI:
# To send an unpublish trigger to the mesh run:
# `streamx unpublish <channel> <messageKey>`

# For more information, see https://www.streamx.dev/guides/streamx-command-line-interface-reference.html#_streamx_unpublish
# or run `streamx unpublish --help`

  KEY=$1

  streamx unpublish $CHANNEL "$KEY"
}

unpublishWithCurl() {
# Unpublishing is also available by sending request directly to Rest Ingestion Service.

  KEY=$1

  curl -w " - status: %{response_code} - $KEY publication\n" -X POST "http://localhost:8080/ingestion/v1/channels/$CHANNEL/messages" \
       -H 'Content-Type: application/json' \
       -d "{
           \"key\" : \"$KEY\",
           \"action\" : \"unpublish\",
           \"eventTime\" : null,
           \"properties\" : { },
           \"payload\" : null
         }"
}

unpublish() {
  unpublishWithCLI "$1"
#  unpublishWithCurl "$1"
}

# Publishing

publish "spray-bottle" "Spray bottle" \
"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent vitae elit eget purus venenatis feugiat eget eget lorem. Donec eu egestas felis, vel viverra urna. Proin pellentesque porta quam, at commodo metus suscipit vel. Cras rhoncus, arcu non dapibus feugiat, felis odio laoreet nunc, in sagittis justo quam quis augue. Nullam tincidunt ante id cursus suscipit. Mauris tortor ipsum, dapibus in condimentum ac, consequat at quam. Nunc nec tristique tortor. Etiam euismod tristique lacus, a sollicitudin odio auctor rhoncus. In blandit ullamcorper eros, in ultricies enim ultrices at." \
"/static/images/product-1.webp"
publish "glossy-plastic-bottle" "Glossy Plastic bottle mockup" \
"Nullam vitae eleifend lectus. Cras dignissim ligula velit, non cursus nibh elementum at. Suspendisse sollicitudin non sem in cursus. Quisque nec efficitur risus. Suspendisse ut ante augue. Cras non eleifend nisl. Cras ut lorem dui. Sed vitae erat ligula. Duis ut diam nec dolor scelerisque pretium non eu mi. Suspendisse potenti. Mauris convallis erat rutrum nulla fermentum egestas. Fusce mollis sit amet purus ut commodo. Etiam varius ornare laoreet." \
"/static/images/product-2.webp"
publish "large-spray-can" "Large Spray can mockup" \
"Pellentesque fermentum a elit placerat porttitor. Morbi molestie dolor et quam tempor, nec suscipit neque hendrerit. Nullam dictum est at ex ornare commodo. Donec convallis ex ligula, in eleifend diam sollicitudin et. Donec porttitor enim bibendum risus rhoncus pellentesque. Mauris a vehicula velit, quis consectetur dolor. Proin porta ac risus in suscipit. Suspendisse nisl nunc, semper ut dapibus eu, congue eu arcu. Nulla facilisi. Donec quis libero vitae metus tristique porttitor quis vitae eros. Nulla feugiat nibh in condimentum venenatis. Aenean mi quam, tempus ac luctus nec, lobortis vel nulla. Ut ultricies mattis turpis, eu blandit nibh imperdiet quis." \
"/static/images/product-3.webp"

# Unpublishing (initially disabled)

#unpublish "spray-bottle"
#unpublish "glossy-plastic-bottle"
#unpublish "large-spray-can"
