;clojure development documentation
(ns esteban.clojure)
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

;;within ASYNC from clojure, comes the concept of atom.
(def first-atom (atom {}))
(swap! first-atom (fn [currentvalue] (assoc currentvalue :a 1)))
;;This concept works completely independently (such as Threads) and therefor are not thought to be synchronised operations. An example of this logic (https://clojure.wladyka.eu/posts/share-state/):
(def counter (atom 0))
(def foo (atom 1))
(defn slow-inc [n]
  (swap! counter inc)
  (Thread/sleep 200)
  (inc n))

(pmap
  (fn [_]
    (swap! foo slow-inc))
  (range 100))

@counter
@foo

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

;;Accessing the VALUE in each future
@nomeassures-simulation
@socialdistancing-simulation


(defn compare-worlds
  [input-world]
  (map (fn [key] (- (key world) (key input-world))) (keys input-world)))


(compare-worlds @nomeassures-simulation)

;;DELAYS Define processes that WILL RUN into another THREAD but will not be inmediatly executed after defining. Here we define a function that creates a DELAY charged with the @VALUE of a simulated-world
(defn tell-difference [simulated-world] (delay (compare-worlds simulated-world)))
;;And then the process can be started whenever you want...one must FORCE it!
(force (tell-difference @socialdistancing-simulation))
(force (tell-difference @nomeassures-simulation))



;;macros were made to improve READABILITY and MANTAINENCE of CODE
(macroexpand '(-> world (nth 1) (nth 1)))
