(ns Algorithms.dynamicconnectivityproblem)  

(def number-of-nodes 10)
(def number-of-bindings 5)

;;the most important condition for the intial process of binding is wether all nodes are connected or not, that will the process flag.
(defn all-connected? [current-universe]
  (if (= (count (filter (fn [atom]
                          (= (count @atom) 1))
                        current-universe))
         0)
    true
    false))

;;at the beginning all the elements in the universe must be unconnected, this may change and therefore the correct structure is an atom. The difference between them will be the key of the dictionary... the starting index in the universe of the atom.
(def universe
  (apply vector
         (map (fn [index]
                (atom {index #{index}})) ;;Each Node has its own ID and a SET as value.
              (range number-of-nodes))))

;;the connections will be randomly made, and only two rules must be followed: An element cannot connect to itself and the same connections should not be repeated. The SET structure makes it easy!

;;The connection is made in pairs. The question is how do we retain and represent that binding... in the atoms themselves
(defn connect [x y]
  (letfn [(step [a b]
            (swap! (nth universe a)
                   (fn [c]
                     (update c a (fn [v]
                                   (conj v b))))))]
    (map step [x y] [y x])))

;;now we can start binding our elements in the universe and seeing what happens! (we use pmap in order to make all bindings at once as a single operation, later on when more bindings need to be made it will be very useful... I suppose)

(pmap (fn [_]
        (let [a (rand-int number-of-nodes)
              b (rand-int number-of-nodes)]
          (connect a b)))
      (range number-of-bindings))

;;leting aside the fact that a binding can be applied to the same pair of atoms, we continue defining the other core ideas of the algorithm and problem:

(defn condition [function]
  (apply vector
         (function (fn [a] (> (count (first (vals a))) 1))  ;;the condition is clear: The number of nodes connected to it.
                   (for [a universe] @a))))

(def connected (partial condition filter))
(def unconnected (partial condition remove))

(connected)

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

(defn render-node
  [bg x y]
  (doto bg
    (.setColor (. Color blue))
    (.fillOval x y 30 30)))

(defn render-connection
  [bg n1 n2]
  (doto bg
    (.setColor (. Color black))
    (.drawLine (+ (first n1) 15) (+ (second n1) 15) (+ (first n2) 15) (+ (second n2) 15))))

(fn
  [bg data]
  (let [parent (first (keys data))
        childs (disj parent (first (vals data)))]
    (map (fn [c]
           (render-connection bg (nth ps i) x))
         childs)))

(defn render [g]
  (let [img (new BufferedImage 800 800 (. BufferedImage TYPE_INT_ARGB))
        bg (. img (getGraphics)) ;;we get the Graphcs2D object
        width (. img (getWidth))
        height (. img (getHeight))
        ps (for [i (range (count universe))] [(rand-int width) (rand-int height)])]  
    (dorun  ;;this is very important... don't completely understand why.
     (for [i ps]
       (render-node bg (first i) (second i))))
    (dorun
     (for [data (connected)]
       (render-connection bg (nth ps (first (keys node))) (nth ps 2))))
    (. g (drawImage img 0 0 nil))  ;;the img generated and modified is gonna be USED BEFORE FINISHING
    (. bg (dispose)))) ;; Via this line, the function render will return the manipulated "image".

(def panel (doto (proxy [JPanel] []
                        (paint [g] (render g)))  ;;paint will be called automatically right after the object was initiallized
             (.setPreferredSize (new Dimension 800 800))))

(def frame (doto (new JFrame) (.add panel) .pack .show))

(. panel (repaint))  ;;for future paintings on the JPanel

(first (keys {:a 2}))

(map vector [1] [2])
