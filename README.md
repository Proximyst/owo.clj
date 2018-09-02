# owo.clj

Clojure library to upload to [OwO.Whats-Th.is](whats-th.is)?

## Usage

To shorten a link: (This does not encode it for HTTP compatibility)

```clojure
(owo.shorten/shorten-url
    token
    (java.net.URLEncoder/encode "https://owo.whats-th.is" "UTF-8")
    ; Past this point, everything is optional
    :user-agent "WhatsThisClientButCooler"
    :api-url "localhost"
    :base "" ; No base, so you'll only get the ID
    )
```

To upload a random byte array:

```clojure
(let [data (byte-array 32)
      filename "test.bin" ; this is defaulted to a.bin
      content-type "application/octet-stream" ; this is defaulted
      ]
      (.nextBytes (ThreadLocalRandom/current) data)
      (owo/upload-data
          token
          (create-multipart {
              :filename filename ; Not necessary (defaults)
              :content-type content-type ; Not necessary (defaults)
              :data data
          })
          )
      )
```

To change any of the variables:
```clojure
(def owo.variables/*api-url* "localhost")
```