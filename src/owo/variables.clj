(ns owo.variables)

(def *api-url*
  "The API url for the library.
  This is by default the API URL to the OwO v1 endpoint."
  "https://api.awau.moe")

(def *user-agent*
  "The User-Agent field to use when accessing the *api-url* using the library."
  (str "WhatsThisClient (https://github.com/proximyst/owo.clj, " owo.constants/VERSION))

(def *base-urls*
  "The base URLs for the library."
  {:shorten "https://awau.moe/"})