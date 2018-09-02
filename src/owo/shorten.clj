(ns owo.shorten
  (:import (owo.exception InvalidTokenException InvalidApiException InternalServerError)
           (java.net URL HttpURLConnection)
           (java.io InputStreamReader BufferedReader)))

(defn shorten-url
  "Shortens the url with the given arguments:

  token - The token used to shorten (given as the Authorized header)
  url - The URL to shorten
  :api-url - The API endpoint to use; defaulted to owo.variables/*api-url*
  :user-agent - The User-Agent header value; defaulted to owo.variables/*user-agent*
  :base - The base URL to add to the shortened ID; defaulted to (:shorten owo.variables/*base-urls*)

  returns: (str base shortened-id)
  throws: InvalidTokenException, InvalidApiException, InternalServerError"
  [token url
   & {:keys [api-url user-agent base]
      :or   {api-url    owo.variables/*api-url*
             user-agent owo.variables/*user-agent*
             base       (:shorten owo.variables/*base-urls*)}}]

  (cond
    (nil? token) (throw (InvalidTokenException.))
    (nil? api-url) (throw (InvalidApiException.)))

  (let [url (URL. (str api-url "/shorten/polr?action=shorten&url=" url))
        connection (cast (.openConnection url) HttpURLConnection)]

    (doto connection
      (.addRequestProperty "User-Agent" user-agent)
      (.addRequestProperty "Authorization" token)
      (.setRequestMethod "GET")
      (.connect)
      #(case (.getResponseCode %)
         401 (throw (InvalidTokenException.))
         500 (throw (InternalServerError.))))

    (let [stream (.getInputStream connection)
          reader (InputStreamReader. stream)
          buf (BufferedReader. reader)]
      (try
        (let [url (.readLine buf)
              id (.substring url (.lastIndexOf url "/"))]
          (str base id))

        (finally
          (when reader
            (.close reader))
          (when buf
            (.close buf)))))))