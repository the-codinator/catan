# Catan client

## Available scripts

- `compile`: Compiles all the Reason code to JS and spits out `.bs.js` files at relevant places
- `start`: Runs `compile` in watch mode
- `build`: Compiles Reason to JS, makes a prod bundle and dumps it in `bundleOutput/index.js`
- `clean`: Purges all the assets
- `dev-server`: Starts dev server on port `8000`
- `prod-server`: Makes a prod bundle and serves on port `5000`

## Local development

1. Install deps:

    ```
    yarn
    ```

2. Start the compiler in watch mode:

    ```
    yarn start
    ```

3. In a separate shell, start the dev server:

    ```
    yarn dev-server
    ```

View the UI at `http://localhost:8000`.

## Deployment

Bundle the app using `yarn build`. Serve the bundle directory in SPA mode.