async function fetchJson(url) {
  try {
    const response = await fetch(url);

    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }
    const json = await response.json();
    const productJsonElement = document.getElementById('productJson');
    productJsonElement.innerHTML = JSON.stringify(json, null, 2)
  } catch (error) {
    console.error(error.message);
  }
}
