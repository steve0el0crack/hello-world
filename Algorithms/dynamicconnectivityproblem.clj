(ns Algorithms.dynamicconnectivityproblem)

(def number-of-elements 10)
(def number-of-bindings 5)

(defn all-connected? [current-universe]
  (if (= (count (filter (fn [atom] (= (count @atom) 1)) current-universe)) 0)
    true
    false))

;;at the beginning all the elements in the universe must be unconnected, this may chane and therefore the correct structure is an atom. The difference between them will be their positions in the universe (index's)
(def universe (apply vector (map (fn [index] (atom #{index})) (range number-of-elements))))

;;The connection is made in pairs. The question is how do we retain and represent that binding... in the atoms themselves
(defn connect
  [x y]
  (let [origin (nth universe x)
        goal (nth universe y)
        i x
        f y]
    (map (fn [pair] (swap! (first pair) (fn [inmediate-neighbours] (conj inmediate-neighbours (second pair)))))
         [[origin f] [goal x]])
    )) 
;;but the connections have 2 conditions: An element cannot connect to itself, and a the operation to connect a pair should not be repeated for the same pair.
(defn generate-pair []
  (loop [output [(rand-int number-of-elements) (rand-int number-of-elements)]]
    (if (= (first output) (second output))
      (recur ([(rand-int number-of-elements) (rand-int number-of-elements)]))
      output)))

;;now we can start binding our elements in the universe and seeing what happens!
(map (fn [_] (connect (first (generate-pair)) (second (generate-pair))))
     (range number-of-bindings))

universe


