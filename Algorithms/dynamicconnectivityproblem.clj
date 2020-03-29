(ns Algorithms.dynamicconnectivityproblem)

(def number-of-elements 10)
(def number-of-bindings 5)

(defn all-connected? [current-universe]
  (if (= (count (filter (fn [atom] (= (count @atom) 1)) current-universe)) 0)
    true
    false))

;;at the beginning all the elements in the universe must be unconnected, this may chane and therefore the correct structure is an atom. The difference between them will be the key of the dictionary... the starting index in the universe of the atom.
(def universe (apply vector (map (fn [index] (atom {index #{index}})) (range number-of-elements))))

;;The connection is made in pairs. The question is how do we retain and represent that binding... in the atoms themselves
(defn connect
  [x y]
  (let [origin (nth universe x)
        destiny (nth universe y)
        i x
        f y]
    (map (fn [data]
           (let [atom (first data)
                 key  (second data)
                 new-val (nth data 2)]
             (swap! atom
                    (fn [atom-value]
                      (update atom-value key
                              (fn [path]
                                (conj path new-val)))))))
         [[origin i f]
          [destiny f i]])))

;;but the connections have 2 conditions: An element cannot connect to itself, and a the operation to connect a pair should not be repeated for the same pair.
(defn generate-pair []
  (loop [output [(rand-int number-of-elements) (rand-int number-of-elements)]]
    (if (= (first output) (second output))
      (recur ([(rand-int number-of-elements) (rand-int number-of-elements)]))
      output)))

;;now we can start binding our elements in the universe and seeing what happens!
(map (fn [_]
       (let [pair (generate-pair)
             origin (first pair)
             goal (second pair)]
         (connect origin goal)))
     (range number-of-bindings))

;;leting aside the fact that a binding can be applied to the same pair of atoms, we continue defining the other core ideas of the algorithm and problem
(defn apply-fn-to-all [function]
  (function (fn [path] (> (count path) 1))
    (apply vector (map (fn [atom] (first (apply vector (vals @atom)))) universe))))
(def get-direct-connections (partial apply-fn-to-all filter))
(def get-unconnected (partial apply-fn-to-all remove))

;;The logic implemented in the atoms is very simple and realistic: They cannot see the network they are building with their connections. But we must be able to do that...
(defn create-network []
  )
