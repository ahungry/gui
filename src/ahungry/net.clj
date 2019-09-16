(ns ahungry.net
  (:require
   [clj-http.client :as client]
   [cheshire.core :as cheshire]))

(defn as-json
  "Convenience / helper to pull some remote URL as JSON.
  Works off the same interface as a clj-http request."
  [f]
  (fn [url opts]
    (->
     (f url (conj {:as :json
                   :coerce :always}
                  opts))
     :body)))

(def get-json (as-json client/get))
(def post-json (as-json client/post))
