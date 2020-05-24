let sessionContext: React.Context.t(option(Auth.session)) = React.createContext(None);

let makeProps = (~session: option(Auth.session), ~children, ()) => {
  "value": session,
  "children": children
};

let make = React.Context.provider(sessionContext);
