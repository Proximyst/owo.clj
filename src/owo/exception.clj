(ns owo.exception)

(gen-class
  :name owo.exception.InvalidTokenException
  :extends IllegalArgumentException
  :init [[] []])

(gen-class
  :name owo.exception.InvalidApiException
  :extends IllegalArgumentException
  :init [[] []])

(gen-class
  :name owo.exception.InternalServerError
  :extends IllegalArgumentException
  :init [[] []])

(gen-class
  :name owo.exception.TooLargePayloadException
  :extends IllegalArgumentException
  :init [[] []])
