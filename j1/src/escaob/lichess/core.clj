(ns escaob.lichess.core
  (:require [org.httpkit.client :as http]  ;; "Like the Server, the client uses an event-driven, non-blocking I/O model." (http://http-kit.github.io/)
            [clojure.data.json :as json])
  (:import [org.httpkit.client ClientSslEngineFactory]))

;; The Lichess API describes more target points than just "/account", for example there is also "/preferences" (https://lichess.org/api#operation/accountEmail). So, I am defining a basis function called query, to which when passed nil as argument; returns "get" for "/account".

(defn query
  [x]
  (let [target (str "https://lichess.org/api/account"
                    (if (= x nil)
                      x
                      (str "/" x)))]
    @(http/get target
               {:headers {"Authorization" "Bearer PAkhhpIdIdKQeBvi"}
                :sslengine 
                (ClientSslEngineFactory/trustAnybody)})))

;; Lichess-data brings a lot of information from my Lichess profile or account (https://lichess.org/@/EstebanRicardo).
;; This is contained within the body of what I receive as response. All the other keys and stuff belongs to httpkit.client properties, for example the "keep-alive" attribute.

=> {:opts {:headers {"Authorization" "Bearer PAkhhpIdIdKQeBvi"},
           :sslengine #object[sun.security.ssl.SSLEngineImpl 0x48476b06 "sun.security.ssl.SSLEngineImpl@48476b06"],
           :method :get,  ;;it would be interesting to see what other methods there are... https://github.com/http-kit/http-kit
           :url "https://lichess.org/api/account"},
    :body "{\"id\":\"estebanricardo\",\"username\":\"EstebanRicardo\",\"online\":true,\"perfs\":{\"blitz\":{\"games\":98,\"rating\":1637,\"rd\":99,\"prog\":2},\"puzzle\":{\"games\":111,\"rating\":1951,\"rd\":161,\"prog\":26,\"prov\":true},\"bullet\":{\"games\":20,\"rating\":1348,\"rd\":106,\"prog\":106},\"correspondence\":{\"games\":10,\"rating\":1361,\"rd\":143,\"prog\":0,\"prov\":true},\"classical\":{\"games\":7,\"rating\":1763,\"rd\":221,\"prog\":0,\"prov\":true},\"rapid\":{\"games\":98,\"rating\":1738,\"rd\":82,\"prog\":-34}},\"createdAt\":1474821213438,\"profile\":{\"country\":\"DE\",\"location\":\"Bonn\",\"bio\":\"A hacker with Clojure, Python, and Java. Of course functional programmer!  GitHub Account\\r\\nFisher and Kasparov are my favorite players, and I would really enjoy analyzing some of their games with other people.\\r\\nHandball player at HSG Siebengebirge.\",\"firstName\":\"Esteban\",\"lastName\":\"Esteban Ricardo\",\"fideRating\":1462,\"links\":\"https://www.chess.com/member/esteban_ricardo\\r\\nhttps://github.com/steve0el0crack\"},\"seenAt\":1591868456735,\"playTime\":{\"total\":145461,\"tv\":0},\"language\":\"en-GB\",\"url\":\"https://lichess.org/@/EstebanRicardo\",\"nbFollowing\":1,\"nbFollowers\":5,\"completionRate\":20,\"count\":{\"all\":269,\"rated\":233,\"ai\":0,\"draw\":6,\"drawH\":6,\"loss\":124,\"lossH\":124,\"win\":139,\"winH\":139,\"bookmark\":1,\"playing\":0,\"import\":0,\"me\":0},\"followable\":true,\"following\":false,\"blocking\":false,\"followsYou\":false}",
    :headers {:date "Thu, 11 Jun 2020 09:41:59 GMT",
              :server "nginx",
              :vary "Origin",
              :expect-ct "max-age=31536000, enforce, report-uri=\"https://monitor.lichess.ovh/report/ct\"",
              :report-to "{\"group\":\"default\",\"max_age\":31536000,\"endpoints\":[{\"url\":\"https://monitor.lichess.ovh/report/default\"}],\"include_subdomains\":true}",
              :x-frame-options "DENY",
              :strict-transport-security "max-age=31536000; includeSubDomains; preload",
              :content-type "application/json",  ;;don't know what json is or how it works...
              :access-control-allow-methods "OPTIONS, GET, POST",  ;;this looks important. 
              :content-encoding "gzip",
              :access-control-allow-origin "*",  ;;this too.
              :connection "keep-alive",
              :x-oauth-scopes "preference:read, email:read, challenge:read, puzzle:read", ;;I've set these option at the moment of creating the token at https://lichess.org/account/oauth/token
              :nel "{\"report_to\":\"default\",\"max_age\":31536000,\"include_subdomains\":true,\"failure_fraction\":0.001}",
              :x-accepted-oauth-scopes "",
              :transfer-encoding "chunked",
              :access-control-allow-headers "Origin, Authorization, If-Modified-Since, Cache-Control"},
    :status 200}

;; As a resume of httpkit.client (http://http-kit.github.io/client.html) I can say that it is a try of integrating the native concepts of asynchronous handling technology of Clojure, into a server & client framework. This enables things like this: http://http-kit.github.io/blog.html. It is actually written in Clojure and Java.
;; It would be very helpful to see how http/get method actually works... (https://github.com/http-kit/http-kit/blob/master/src/org/httpkit/client.clj)

;; Anyway, what matters the most is the :body part of the response. My programm must be able to read & interpret it and then constructs useful representations of this data on some browser of mobile app. I am gonna explore these tools... 

;; The returned value is in json format, and therefore I must depure it in order to get something readable.

(defn depure-query
  [x]
  (->
   (query x)
   :body
   (json/read-str :key-fn keyword)))

=> {:completionRate 20,
    :playTime {:total 145461, :tv 0},
    :nbFollowing 1,
    :username "EstebanRicardo",
    :nbFollowers 5,
    :createdAt 1474821213438,
    :following false,
    :online true,
    :perfs {:blitz {:games 98, :rating 1637, :rd 99, :prog 2},
            :puzzle {:games 111, :rating 1951, :rd 161, :prog 26, :prov true},
            :bullet {:games 20, :rating 1348, :rd 106, :prog 106},
            :correspondence {:games 10, :rating 1361, :rd 143, :prog 0, :prov true},
            :classical {:games 7, :rating 1763, :rd 221, :prog 0, :prov true},
            :rapid {:games 98, :rating 1738, :rd 82, :prog -34}},
    :followable true,
    :language "en-GB",
    :id "estebanricardo",
    :count {:bookmark 1, :winH 139, :ai 0, :lossH 124, :all 269, :rated 233, :drawH 6, :playing 0, :draw 6, :loss 124, :import 0, :me 0, :win 139},
    :url "https://lichess.org/@/EstebanRicardo",
    :followsYou false,
    :blocking false,
    :seenAt 1591872353001,
    :profile {:country "DE", :location "Bonn", :bio "A hacker with Clojure, Python, and Java. Of course functional programmer!  GitHub Account\r\nFisher and Kasparov are my favorite players, and I would really enjoy analyzing some of their games with other people.\r\nHandball player at HSG Siebengebirge.",
              :firstName "Esteban",
              :lastName "Esteban Ricardo",
              :fideRating 1462,
              :links "https://www.chess.com/member/esteban_ricardo\r\nhttps://github.com/steve0el0crack"}}

;; And here will be calculated the number of total games I've ever played on Lichess.

(let [data (depure-query nil)]
    (->>
     (map
      (fn [game-type]
        (get-in data [:perfs game-type :games]))
      (keys (get-in data [:perfs])))
     (reduce + 0 )))







