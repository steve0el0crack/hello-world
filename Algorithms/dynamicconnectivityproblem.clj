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

;;now we can start binding our elements in the universe and seeing what happens! (we use pmap in order to make all bindings at once as a single operation, later on when more bindings need to be made it will be very useful... I suppose)
(pmap (fn [_]
        (let [pair (generate-pair)
              origin (first pair)
              goal (second pair)]
          (connect origin goal)))
      (range number-of-bindings))

;;leting aside the fact that a binding can be applied to the same pair of atoms, we continue defining the other core ideas of the algorithm and problem
(defn apply-fn-to-all [function]
  (apply vector
         (function (fn [atom-value] (> (count (first (vals atom-value))) 1))
                   (apply vector (map (fn [atom] @atom) universe)))))
(def get-all-direct-connections (partial apply-fn-to-all filter))
(def get-all-unconnected (partial apply-fn-to-all remove))

;;In order to adress the question, very directly:
(defn get-neighbours [index]
  (first (vals @(nth universe index))))

;;In maps, the key is the key, and in arrays it is its index's, there cannot use contains?
(defn check-in-coll [request coll]
  (some (fn [key] (= request key)) coll))

;;I will define a separate process for searching an element in a network, with an starting point.
(defn search-in-network
  [request
   start-point]
  (loop [initial-stack (get-neighbours start-point)]
    (if (check-in-coll request initial-stack)
      "founded")))

(defn is-connected?
  [first-index
   second-index]
  (let [unconnected (flatten (map keys (get-all-unconnected)))
        connected (flatten (map keys (get-all-direct-connections)))]
    (if (= '(nil true nil true) (for [ atom [first-index second-index] coll [unconnected connected]]
                                  (check-in-coll atom coll)))
      (if (check-in-coll first-index (first (vals @(nth universe second-index))))
        ;;(search-in-network a b)
        "Direct connection")
      "Not connected at all")))

(is-connected? 1 3)
(get-all-direct-connections)
(get-all-unconnected)
(map (fn [node] @node) universe)


(def a (agent []))
(send a conj 1)


;;***************************** UI ***********************************************
;;I am gonna show how these graph will be building, just like a simulation.

(import
 '(java.awt Dimension Color Graphics)
 '(javax.swing JPanel JFrame)
 '(java.awt.image BufferedImage)
 '(java.awt.geom Ellipse2D))

;;I want to first see the N elements isolated and not connected at all. And then, as time comes; see how the connections will be builded.


(defn render [g]
  (let [img (new BufferedImage 300 300 (. BufferedImage TYPE_INT_ARGB))
        bg (. img (getGraphics))]  ;;we get the Graphcs2D object
    (doto bg
      (.setColor (. Color blue))
      (.draw (new () (50, 50, 50, 50))))
    (. g (drawImage img 0 0 nil))  ;;the img generated and modified is gonna be USED BEFORE FINISHING
    (. bg (dispose)))) ;;for efficiency, programmers should call *dispose* when finished using a Graphics object.

(def panel (doto (proxy [JPanel] []
                        (paint [g] (render g)))  ;;paint will be called automatically right after the object was initiallized
             (.setPreferredSize (new Dimension 300 300))))

(def frame (doto (new JFrame) (.add panel) .pack .show))

(. panel (repaint))  ;;for future paintings on the JPanel
