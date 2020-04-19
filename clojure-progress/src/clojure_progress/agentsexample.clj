(ns clojure-progress.agentsexample)

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



;;There's another use  for agents and it is converting them into managers of many asynchronized proceses, simulating a watterfall-pipeline process. The example comes fom https://clojuredocs.org/clojure.core/*agent* and the idea itself was also used 
(def myagent (agent 0))
(defn modified-inc [x]
  (. Thread (sleep 1000))
  (inc x))

(send-off myagent modified-inc)  ;;The logic of send-off is just to return INMEDIATLY the current value of the agent, even before he executes the proccess send to him. We are gonna make use of this to create a complete isolated process that will increment the agent value eternally.

(def running true)  ;;first whe define a condition
(defn eternal-inc [x]
  (when running
    (send-off *agent* eternal-inc))  ;;creating another call, that the agent will manage by himself. Here's the beaty of it: The change on that first thread must be executed, and then comes the next. 
  (. Thread (sleep 1000))
  (inc x))

(send-off myagent eternal-inc)  ;;this should return 0, and then go on. If later we dereference the agent, we will get another value 'cause the proccess' on!

(def running false)  ;;for stopping it!
(shutdown-agents)  ;;This disconnect the cider too.
