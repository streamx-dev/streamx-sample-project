# StreamX Project Template

This project contains sample implementations of StreamX to demonstrate how to use StreamX.
These services can be used to create sample StreamX mesh.

Please note that these services are not production-ready!

## Project structure

Each StreamX project should be organized in a specific way and should follow the conventions described below.

The following are recommended directories:

[//]: # (* TODO `configs`)
[//]: # (* TODO `secrets`)
* [scripts](./scripts/README.md) - all useful scripts and resources should be placed here
* [services](./services/README.md) - your StreamX Processing and Delivery Services (as well as other Maven modules) should be placed here


[//]: # (* TODO `mesh.yaml`)
[//]: # (* TODO `pom.xml`)
[//]: # (* TODO `deployment.yaml`)

## Prerequisites

To work with this repository you need:
* OpenJDK 17+ installed with JAVA_HOME configured appropriately
* Docker
* StreamX CLI

### StreamX CLI

To install StreamX CLI, run:
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

For more information, visit [StreamX CLI Reference](https://www.streamx.dev/guides/streamx-command-line-interface-reference.html#_installing_the_cli).

## Packaging

To build StreamX project, run:
```shell
# For Linux/MacOS
./mvnw clean install
```
or 
```shell
# For Windows
mvnw.cmd clean install
```

This command builds both the Maven artefacts and the Docker images needed to start the local StreamX mesh.

## Running local StreamX Mesh

To start local instance of Mesh run:

```shell
streamx run
```
The above command runs the StreamX mesh defined in the `mesh.yaml` file located in the current directory.
For more information, visit [StreamX CLI Reference](https://www.streamx.dev/guides/streamx-command-line-interface-reference.html#_streamx_run).

## Ingesting local mesh with sample data

Optionally run script to trigger sample publications and see results:

```bash
sh scripts/publish-all.sh
```

To see results of publication - visit:
* [Generated pages](http://localhost:8081/products.html) 
* [GraphQL Console](http://localhost:8084/q/graphql-ui/) and launch some queries (details in [GraphqlDeliveryService](./services/graphql-delivery-service/README.md))
