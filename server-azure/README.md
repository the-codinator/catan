# Catan Server

[![Code Style: Google](https://img.shields.io/badge/code%20style-google-blueviolet.svg)](https://github.com/google/gts)

## Hosting Specific Implementation

This is a Azure Function version of the generic code implemented at [server](../server/README.md)

## Local Development

1. Setup `local.settings.json` (see below)
1. Run `npm install` to setup dependencies
1. Start application with `npm start`
1. Your application will be running at `https://localhost:8080`

### Local Settings

Following is the template for the `local.settings.json`

```json
{
  "IsEncrypted": false,
  "Values": {
    "CATAN_COSMOSDB_ENDPOINT": "",
    "CATAN_COSMOSDB_KEY": "",
    "FUNCTIONS_EXTENSION_VERSION": "~3",
    "FUNCTIONS_WORKER_RUNTIME": "node",
    "WEBSITE_NODE_DEFAULT_VERSION": "~12"
  }
}
```

Fill in the `endpoint` and `key` values from the cosmos db credentials in the azure portal, or values for the local cosmos db emulator.

## Swagger

Swagger UI is available to play with the APIs at `/swagger`
