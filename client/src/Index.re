// Entry point
[@bs.val] external document: Js.t({..}) = "document";

// create empty div and inject in dom
let makeContainer = () => {
  let container = document##createElement("div");
  container##className #= "container";
  document##body##appendChild(container) |> ignore;
  container;
};

// render the app in the empty div
ReactDOMRe.render(
  (
    <AuthBounday>
      <Router/>
    </AuthBounday>
  ),
  makeContainer(),
);
