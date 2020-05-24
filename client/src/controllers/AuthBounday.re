[@react.component]
let make = (~children: ReasonReact.reactElement) => {

  let (session, setSession) = React.useState(() => None);
  let (authhorizing, setAuthorising) = React.useState(() => true);

  React.useEffect0(() => {
    setAuthorising((_) => true);
    open Js.Promise;
    Auth.authorize()
    |> then_(sessionInfo => {
      setSession((_) => sessionInfo);
      setAuthorising((_) => false);
      resolve();
    })
    |> catch((_) => {
      setAuthorising((_) => false);
      resolve();
    }) |> ignore;
    None
  });

  if (authhorizing) {
    <div>
      {ReasonReact.string("Authorising ...")}      
    </div>
  } else {
    <SessionProvider session={session}>
      {ReasonReact.string("Navbar")}
      {children}
    </SessionProvider>
  }

};