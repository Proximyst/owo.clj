(ns owo.constants
  (:import (com.google.gson GsonBuilder Gson)))

(def VERSION
  "The version of the library."
  "0.1.0")

(def GSON
  ^Gson
  "The GSON instance used by the library."
  (-> (GsonBuilder.)
      (.disableHtmlEscaping)
      (.setLenient)
      (.create)))