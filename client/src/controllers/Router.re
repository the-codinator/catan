type route =
  | RAuth | RHome | RCreate | RPlay(string);

// function to change the route
let push = (route: route) => {
  switch (route) {
    | RAuth => "/login"
    | RHome => "/"
    | RCreate => "/create"
    | RPlay(gameId) => "/play/" ++ gameId
  } |> ReasonReactRouter.push;
  ReasonReact.null
};

// make a route such that all it's children are redirected to it
let makeSingleRoute = (route: route, component: ReasonReact.reactElement, subPath) =>
  switch (subPath) {
  | [] => component
  | _ => {
    push(route)
  }
};

// router component
[@react.component]
let make = () => {

  // get current route state
  let currentUrl = ReasonReactRouter.useUrl();

  // check session
  let sessionContext = React.useContext(SessionProvider.sessionContext);

  // render route based on the path and session
  switch (currentUrl.path, sessionContext) {
    | (["login", ...subPath], None) => makeSingleRoute(RAuth, <Login/>, subPath)
    | ([], None) => <Home/>
    | (_, None) => {
      push(RAuth);
    }
    | (path, _) => switch (path) {
      | [] => <Home />
      | ["create", ...subPath] => makeSingleRoute(RCreate, <Create/>, subPath)
      | ["login", ..._] => {
        push(RHome);
      }
      | ["play", ...subPath] => switch (subPath) {
        | [gameId, ...subSubPath] => makeSingleRoute(RPlay(gameId), <Play gameId={gameId}/>, subSubPath)
        | _ => {
          push(RHome);
        }
      }
      | _ => {
        push(RHome)
      }
    }
  }
}