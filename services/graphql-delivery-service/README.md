# GraphQL Delivery Service

This project contains example StreamX services implementations dedicated to be a showcase of how to develop services.
These services can be used to create example StreamX mesh.

## Prerequisites

To work with this repository you need:
* OpenJDK 17+ installed with JAVA_HOME configured appropriately
* Docker
* StreamX CLI

### StreamX CLI

To install StreamX CLI execute:
```sh
# for Linux/MacOS
brew install streamx-dev/tap/streamx
```
or
```shell
# for Windows
scoop bucket add streamx-dev https://github.com/streamx-dev/scoop-streamx-dev.git
scoop install streamx
```

For more information visit [StreamX CLI Reference](https://www.streamx.dev/guides/streamx-command-line-interface-reference.html).

## Packaging

To build StreamX Project run:
```shell
# For Linux/MacOS
./mvnw clean install
```
or 
```shell
# For Windows
mvnw.cmd clean install
```

This command build both Maven Artefacts and Docker Image required for starting local StreamX Mesh.

## Running local StreamX Mesh

To start local instance of Mesh run:

```shell
streamx run
```
The command above runs the StreamX Mesh defined in the `mesh.yaml` file located in current directory.

Optionally run script to trigger example publications and see results:

```bash
sh scripts/publish-all.sh
```

