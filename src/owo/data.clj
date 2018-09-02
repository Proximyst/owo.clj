(ns owo.data
  (:import (java.io InputStreamReader OutputStream ByteArrayOutputStream)
           (owo.exception InternalServerError InvalidTokenException InvalidApiException TooLargePayloadException)
           (java.net URL HttpURLConnection)
           (java.util Map)
           (com.google.gson Gson) ; resolve .fromJson
           (java.util.concurrent ThreadLocalRandom)))

(defn upload-data
  "Uploads the data given with the following arguments:

  token - The token used to upload (given as the Authorized header)
  bytes - The data to send
  :api-url - The API endpoint to use; defaulted to owo.variables/*api-url*
  :user-agent - The User-Agent header value; defaulted to owo.variables/*user-agent*
  :base - The base URL to add to the uploaded ID; defaulted to (:shorten owo.variables/*base-urls*)

  returns: {:success <true/false>
            :files [{:hash ^String :name ^String :url ^String :size ^Int}]}
  throws: InvalidTokenException, InvalidApiException, InternalServerError, TooLargePayloadException

  Do not this does not add the multi-part headers. Use the create-multipart function for that."
  [token bytes
   & {:keys [api-url user-agent]
      :or   {api-url    owo.variables/*api-url*
             user-agent owo.variables/*user-agent*}}]

  (cond
    (nil? token) (throw (InvalidTokenException.))
    (nil? api-url) (throw (InvalidApiException.)))

  (let [url (URL. (str api-url "/upload/pomf"))
        connection (cast (.openConnection url) HttpURLConnection)]

    (doto connection
      (.addRequestProperty "User-Agent" user-agent)
      (.addRequestProperty "Authorization" token)
      (.addRequestProperty "Content-Type" "multipart/form-data")
      (.setRequestMethod "POST")
      #(doto (.getOutputStream %)
         (.write (byte-array bytes))
         (.flush)
         (.close))
      (.connect)
      #(case (.getResponseCode %)
         401 (throw (InvalidTokenException.))
         413 (throw (TooLargePayloadException.))
         500 (throw (InternalServerError.))))

    (let [stream (.getInputStream connection)
          reader (InputStreamReader. stream)]
      (try
        (let [map (-> owo.constants/GSON
                      (.fromJson reader Map))]
          map)

        (finally
          (when reader
            (.close reader)))))))

(defn create-multipart
  "Creates a multipart byte array for the maps given.
  They must have the following structure:

  {
    :filename ^String = The file name on disk; used for extension on the server (default: a.bin)
    :content-type ^String/^ Keyword = Content-Type name of the file (default: application/octet-stream)
    :data ^byte[] = Byte data to upload
  }"
  [& maps]
  (let [boundary (byte-array 16)
        stream (ByteArrayOutputStream.)]
    (.nextBytes (ThreadLocalRandom/current) boundary)
    (loop [map maps]
      (.write stream boundary)
      (.write stream (.getBytes (str "Content-Disposition: form-data; name=\"files[]\"; filename=" (or (:filename map) "a.bin") "\n")))
      (.write stream (.getBytes (str "Content-Type: " (or (name (:content-type map)) "application/octet-stream") "\n")))
      (.write stream (:data map)))
    (.write stream boundary)

    (.toByteArray stream)))