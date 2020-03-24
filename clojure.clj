;clojure development documentation

(def world
  {:population 1000
   :infected 0
   :dead 0
   :recovered 0
   :healthcapacity 20})

(defn hoch [x a]
  (let [base x]
    (loop [result 1
           counter a]
      (if (= counter 0)
        result
        (recur (* result base) (- counter 1))))))

(defn infected-expectation
  [time
   cst]
  (* (hoch time 2) cst))
(defn dead-expectation
  [infected]
  (* infected (/ 4 100)))

()

(def time 20)

(def first-simulation (promise))
(def second-simulation (promise))

(let [a first-simulation
      b second-simulation]
  (.start (Thread. (fn [] (let [constant (/ 1 9)
                                infected  (infected-expectation time constant)
                                dead (dead-expectation infected)]
                            (deliver a
                                     (assoc world :infected infected :dead dead))))))
  (.start (Thread. (fn [] (let [constant (/ 1 4)
                                infected  (infected-expectation time constant)
                                dead (dead-expectation infected)]
                            (deliver b
                                     (assoc world :infected infected :dead dead)))))))

(deref first-simulation)
(deref second-simulation)
