# catan
Catan the game! [Official Website](https://www.catan.com/game/catan)

Status: In development

Contents:

- [server](./server): Dropwizard based Java service to work as a docker container based server. Did this to learn Dropwizard, but haven't deployed this anywhere coz it was costing too much. Integrated with an in-memory DB for testing purposes.
- [server-azure](./server-azure): Node JS based server. Replicates all logic from the Java service (for the most part) and integrates with Azure Cosmos DB. Ported the Java code here to deal with the high cost problem. Runs on Azure Function Apps so we don't get billed as long as we are within the free-tier.
- ui: currently just as a branch. Meant to be a UI in React JS for displaying the board and gameplay.

TODO:

- UI
- Better auth (currently its stupid without proper encryption n stuff) should integrate with Google / GitHub OAuth
- A bunch of other stuff (see Project board for issues)
