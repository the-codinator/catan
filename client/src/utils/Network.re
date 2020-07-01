open Fetch;

let baseURL = "https://www.google.com";

let makeGetRequest = (
  endpoint: string
) => {
  fetchWithInit(
    baseURL ++ endpoint,
    RequestInit.make(
      ~method_=Get,
      ~headers=HeadersInit.make(Auth.getAuthHeaders()),
      ()
    ),
  );
};
