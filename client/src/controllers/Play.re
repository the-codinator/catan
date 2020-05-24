[@react.component]
let make = (~gameId) => {
  <div>
    {ReasonReact.string("Playing game " ++ gameId)}
  </div>
};