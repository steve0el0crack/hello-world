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

(let [a 10
      b (+ a 10)
      c 12]
  (+ a b c))

(def time 20)

(def first-simulation (promise))

;;possible scenarios of the virus spreading
(def no-meassures (Thread. (fn [_]
                             (let [cst (/ 1 9)
                                   variation (infected-expectation time cst)
                                   initialpopulation (:population world)]
                               (deliver first-simulation (assoc world :infected variation :population (- initialpopulation variation)))))))

(def social-isolating (Thread. (fn [_] (println "t2"))))

(.start no-meassures)

(deref first-simulation)

(.join no-meassures)  ;;stop everything till t1 finishes and return nil

