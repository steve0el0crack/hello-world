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

(def time 20)

;;future will declare its own promise and initialize its own Thread and then deliver the resulting value to the promise, that actually is refered by the future with the name you give to it.
;;The Threads will act independently of the main Thread, but delivering values to the promises declared before.
(def nomeassures-simulation
  (future
    (let [constant (/ 1 4)
          infected  (infected-expectation time constant)
          dead (dead-expectation infected)]
      (assoc world :population (- (:population world) dead) :infected infected :dead dead))))
(def socialdistancing-simulation
  (future
    (let [constant (/ 1 9)
          infected  (infected-expectation time constant)
          dead (dead-expectation infected)]
      (assoc world :population (- (:population world) dead) :infected infected :dead dead))))

;;Accessing the value in each promise
(@nomeassures-simulation)
(@socialdistancing-simulation)