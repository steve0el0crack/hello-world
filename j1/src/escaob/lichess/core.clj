(ns escaob.lichess.core
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json])
  (:import [org.httpkit.client ClientSslEngineFactory]))

(comment
  ;; -- Query 1
  (def lichess-data
    (->
     @(http/get "https://lichess.org/api/account"
                {:headers {"Authorization" "Bearer PAkhhpIdIdKQeBvi"
                           }
                 :sslengine 
                 (ClientSslEngineFactory/trustAnybody)})
     :body
     (json/read-str :key-fn keyword)
     ))
  ;;
  (->>
   (map
    (fn [game-type]
      (get-in lichess-data [:perfs game-type :games]))
    (keys (get-in lichess-data [:perfs])))
   (reduce + 0 ))
  
  )
