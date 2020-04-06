(ns hello-world.agentsexample)

;;This is the first agent, acting as an external visualizator of the embedding process that will take part.
(def logger (agent (list)))
;;The way he reports us his view is via this function
(defn log [msg]
  (send logger (fn [a b] (cons b a)) msg))

;;This creates an n-depth agent (holding other agents as values), what will be used as the pipeline of functions and modifications
(defn create-relay [n]
  (letfn [(next-agent [previous _] (agent previous))]
    (reduce next-agent nil (range 0 n))))

;;Recursive function that ends when the most intrinsic agent is reached (with "nil" as value)
(defn relay [relay msg]
  (letfn [(relay-msg [next-actor hop msg]  ;;hop ... may also be COUNTER
            (cond (nil? next-actor) (log "finished relay")  ;;End
                  :else (do (log (list hop msg))  ;;Activates the viewers function that tells the actual depth
                            (send next-actor relay-msg (+ hop 1) msg))))]  ;;Recursive
    (send relay relay-msg 0 msg)))  ;;starts with 0 als 


(relay (create-relay 10) "hello")
(. java.lang.Thread sleep 5000)
(prn @logger)

(shutdown-agents)

