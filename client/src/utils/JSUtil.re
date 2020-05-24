[@bs.val] [@bs.scope ("window", "localStorage")] external setLSItem: (string, string) => unit = "setItem";
[@bs.val] [@bs.scope ("window", "localStorage")] external getLSItem: (string) => option(string) = "getItem";
[@bs.val] [@bs.scope ("window", "localStorage")] external removeLSItem: (string) => unit = "removeItem";

let joinList = (l: list(string), delim: string) => {
  l |> Array.of_list
    |> Js.Array.joinWith(delim)
};