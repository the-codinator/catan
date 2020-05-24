type session = {
  token: string,
  username: string,
  userId: string
};


let authLSIdentifier = "CATAN_ACCESS_TOKEN";
let getAuthHeaders = () => {
    let accessToken = JSUtil.getLSItem("ooolala");
    {
      "authorization": switch accessToken {
      | Some(token) => Some("Bearer " ++ token)
      | None => None
      }
    }
};

let authorize = () => {
  let sessionInfo = {
    token: "sample token",
    username: "rishichandra",
    userId: "myuserid"
  };
  Js.Promise.resolve(None)
};
