const productFragmentTemplate = `
<div class="group relative">
  <div
      class="aspect-h-1 aspect-w-1 w-full overflow-hidden rounded-md bg-gray-200 lg:aspect-none group-hover:opacity-75 lg:h-80">
    <img src="\${product.imageUrl}" alt="\${product.name}"
         class="h-full w-full object-cover object-center lg:h-full lg:w-full">
  </div>
  <div class="mt-4 flex justify-between">
    <div>
      <h3 class="text-sm text-gray-700">
        <a href="/products/\${product.key}.html">
          <span aria-hidden="true" class="absolute inset-0"></span>
          \${product.name}
        </a>
      </h3>
    </div>
  </div>
</div>`;

function mapProduct(product) {
  return productFragmentTemplate
      .replaceAll('\${product.key}', product.key)
      .replaceAll('\${product.name}', product.name)
      .replaceAll('\${product.imageUrl}', product.imageUrl)
}

async function fetchDataFromGraphQL() {
  const url = "http://localhost:8084/graphql";
  try {
    const queryAllBody =
        {
          "query": "{\n  allProducts {\n    key\n    name\n    imageUrl\n  }\n}"
        };
    const response = await fetch(url, {
      body: JSON.stringify(queryAllBody),
      method: 'POST'
    });
    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

    const json = await response.json();
    const productsByGraphQLElement = document.getElementById('productsByGraphQL');
    productsByGraphQLElement.innerHTML = json.data.allProducts
        .map((e) => { return mapProduct(e); })
        .join('\n');
  } catch (error) {
    console.error(error.message);
  }
}

setTimeout(() => fetchDataFromGraphQL(), 1000)
