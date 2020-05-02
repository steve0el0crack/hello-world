(ns hello-world.ants)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; ant sim ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;   Copyright (c) Rich Hickey. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Common Public License 1.0 (http://opensource.org/licenses/cpl.php)
`;   which can be found in the file CPL.TXT at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

;dimensions of square world
(def dim 80)
;number of ants = nants-sqrt^2
(def nants-sqrt 7)
;number of places with food
(def food-places 35)
;range of amount of food at a place
(def food-range 100)
;scale factor for pheromone drawing
(def pher-scale 20.0)
;scale factor for food drawing
(def food-scale 30.0)
;evaporation rate
(def evap-rate 0.99)

(def animation-sleep-ms 100)
(def ant-sleep-ms 40)
(def evap-sleep-ms 1000)

(def running true)

(defstruct cell :food :pher) ;may also have :ant and :home

;world is a 2d vector of refs to cells
(def world 
     (apply vector 
            (map (fn [_] 
                   (apply vector (map (fn [_] (ref (struct cell 0 0))) ;;the 2 basic variables are FOOD and PHER
                                      (range dim)))) 
                 (range dim))))

(defn place [[x y]]
  (-> world (nth x) (nth y)))

(defstruct ant :dir) ;may also have :food

(defn create-ant 
  "create an ant at the location, returning an ant agent on the location"
  [loc dir]
    (sync nil
      (let [p (place loc)
            a (struct ant dir)]
        (alter p assoc :ant a)  ;;the STRUCT will be associated to the ref (place)
        (agent loc))))  ;;the AGENT ant will return with one value. What do we need to control an ant? Just its location, and the function to apply.

(def home-off (/ dim 4))
(def home-range (range home-off (+ nants-sqrt home-off)))

(defn setup 
  "places initial food and ants, returns seq of ant agents"
  []
  (sync nil  ;;nil acts like a flag that is just ignored. there is an order in this simulation. No need to coordinate at this instant of time: Setup will be FIRST runned.
    (dotimes [i food-places]
      (let [p (place [(rand-int dim) (rand-int dim)])]
        (alter p assoc :food (rand-int food-range))))
    (doall
     (for [x home-range y home-range]
       (do
         (alter (place [x y]) 
                assoc :home true)
         (create-ant [x y] (rand-int 8)))))))

(defn bound 
  "returns n wrapped into range 0-b"
  [b n]
  (rem n b))

(defn wrand 
  "given a vector of slice sizes, returns the index of a slice given a
  random spin of a roulette wheel with compartments proportional to
  slices."
  [slices]
  (let [total (reduce + slices)
        r (rand total)]
    (loop [i 0 sum 0]
      (if (< r (+ (slices i) sum))
        i
        (recur (inc i) (+ (slices i) sum))))))

;dirs are 0-7, starting at north and going clockwise
;these are the deltas in order to move one step in given dir
(def dir-delta {0 [0 -1]
                1 [1 -1]
                2 [1 0]
                3 [1 1]
                4 [0 1]
                5 [-1 1]
                6 [-1 0]
                7 [-1 -1]})

(defn delta-loc 
  "returns the location one step in the given dir. Note the world is a torus"
  [[x y] dir]
    (let [[dx dy] (dir-delta (bound 8 dir))]
      [(bound dim (+ x dx)) (bound dim (+ y dy))]))

;(defmacro dosync [& body]
;  `(sync nil ~@body))

;ant agent functions
;an ant agent tracks the location of an ant, and controls the behavior of 
;the ant at that location

(defn turn 
  "turns the ant at the location by the given amount"
  [loc amt]
    (dosync
     (let [p (place loc)
           ant (:ant @p)]
       (alter p assoc :ant (assoc ant :dir (bound 8 (+ (:dir ant) amt))))))
    loc)

(defn move 
  "moves the ant in the direction it is heading. Must be called in a
  transaction that has verified the way is clear"
  [loc]
     (let [oldp (place loc)
           ant (:ant @oldp)
           newloc (delta-loc loc (:dir ant))
           p (place newloc)]
         ;move the ant
       (alter p assoc :ant ant)
       (alter oldp dissoc :ant)
         ;leave pheromone trail
       (when-not (:home @oldp)
         (alter oldp assoc :pher (inc (:pher @oldp))))
       newloc))

(defn take-food [loc]
  "Takes one food from current location. Must be called in a
  transaction that has verified there is food available"
  (let [p (place loc)
        ant (:ant @p)]    
    (alter p assoc 
           :food (dec (:food @p))
           :ant (assoc ant :food true))
    loc))

(defn drop-food [loc]
  "Drops food at current location. Must be called in a
  transaction that has verified the ant has food"
  (let [p (place loc)
        ant (:ant @p)]    
    (alter p assoc 
           :food (inc (:food @p))
           :ant (dissoc ant :food))
    loc))

(defn rank-by 
  "returns a map of xs to their 1-based rank when sorted by keyfn"
  [keyfn xs]
  (let [sorted (sort-by (comp float keyfn) xs)]
    (reduce (fn [ret i] (assoc ret (nth sorted i) (inc i)))
            {} (range (count sorted)))))

(defn behave 
  "the main function for the ant agent"
  [loc]
  (let [p (place loc)
        ant (:ant @p)
        ahead (place (delta-loc loc (:dir ant)))
        ahead-left (place (delta-loc loc (dec (:dir ant))))
        ahead-right (place (delta-loc loc (inc (:dir ant))))
        places [ahead ahead-left ahead-right]]
    (. Thread (sleep ant-sleep-ms))  ;;This time the "timing" of the ant comes before.
    (dosync  ;;here will be defined the conditions of the TRANSACTIONS, but the actions were defined in TURN, MOVE, TAKE-FOOD, DROP-FOOD. 
     (when running
       (send-off *agent* #'behave))  ;;here the typical recursive infinite call to behave using AGENT as a manager of these calls, and creating separete threads where this fn are going to be runned.
     ;;there are two main behaviours defined by this IF
     (if
         ;;I DO HAVE FOOD (going home)
         (:food ant) 
       (cond 
         (:home @p) (-> loc drop-food (turn 4))  ;;use of threading narrow macro for droping food and then changing the value of the :ant STRUCT in the REF, 180 grades.
         (and (:home @ahead) (not (:ant @ahead))) (move loc)
         :else
         (let [ranks (merge-with + 
                        (rank-by (comp #(if (:home %) 1 0) deref) places)
                        (rank-by (comp :pher deref) places))]
          (([move #(turn % -1) #(turn % 1)]
            (wrand [(if (:ant @ahead) 0 (ranks ahead)) 
                    (ranks ahead-left) (ranks ahead-right)]))
           loc)))
        ;;I DO NOT HAVE FOOD (foraging)
       (cond 
        (and (pos? (:food @p)) (not (:home @p))) 
          (-> loc take-food (turn 4))
        (and (pos? (:food @ahead)) (not (:home @ahead)) (not (:ant @ahead)))
          (move loc)
        :else
          (let [ranks (merge-with + 
                                  (rank-by (comp :food deref) places)
                                  (rank-by (comp :pher deref) places))]
          (([move #(turn % -1) #(turn % 1)]
            (wrand [(if (:ant @ahead) 0 (ranks ahead)) 
                    (ranks ahead-left) (ranks ahead-right)]))
           loc)))))))

(defn evaporate 
  "causes all the pheromones to evaporate a bit"
  []
  (dorun 
   (for [x (range dim) y (range dim)]
     (dosync 
      (let [p (place [x y])]
        (alter p assoc :pher (* evap-rate (:pher @p))))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; UI ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(import 
 '(java.awt Color Graphics Dimension)
 '(java.awt.image BufferedImage)
 '(javax.swing JPanel JFrame))

;pixels per world cell
(def scale 5)

(defn fill-cell [#^Graphics g x y c]
  (doto g
    (.setColor c)
    (.fillRect (* x scale) (* y scale) scale scale)))

(defn render-ant [ant #^Graphics g x y]
  (let [black (. (new Color 0 0 0 255) (getRGB))
        gray (. (new Color 100 100 100 255) (getRGB))
        red (. (new Color 255 0 0 255) (getRGB))
        [hx hy tx ty] ({0 [2 0 2 4] 
                        1 [4 0 0 4] 
                        2 [4 2 0 2] 
                        3 [4 4 0 0] 
                        4 [2 4 2 0] 
                        5 [0 4 4 0] 
                        6 [0 2 4 2] 
                        7 [0 0 4 4]}
                       (:dir ant))]
    (doto g
      (.setColor (if (:food ant) 
                  (new Color 255 0 0 255) 
                  (new Color 0 0 0 255)))
      (.drawLine (+ hx (* x scale)) (+ hy (* y scale)) 
                (+ tx (* x scale)) (+ ty (* y scale))))))

(defn render-place [g p x y]
  ;;both places with phers and food may change their value and therefore their color must be repeateadly calculated
  ;;
  (when (pos? (:pher p))
    (fill-cell g x y (new Color 0 255 0 
                          (int (min 255 (* 255 (/ (:pher p) pher-scale)))))))
  (when (pos? (:food p))
    (fill-cell g x y (new Color 255 0 0 
                          (int (min 255 (* 255 (/ (:food p) food-scale)))))))
  ;;rendering an ant is something completely different as phers and food
  (when (:ant p)
    (render-ant (:ant p) g x y)))  

(defn render [g]
  (let [v (dosync (apply vector (for [x (range dim) y (range dim)] 
                                   @(place [x y]))))  ;;a "photo" of how ALL the refs is taken and their values are stocked into an array
        img (new BufferedImage (* scale dim) (* scale dim) 
                 (. BufferedImage TYPE_INT_ARGB)) 
        ;;. is part of Clojure Java Interop and means "in the scope of"...
        bg (. img (getGraphics))]  
    ;;There are 2 modifications to bg, what seems to be an Object Oriented character. And in between a call to render-place.
    (doto bg
      (.setColor (. Color white))
      (.fillRect 0 0 (. img (getWidth)) (. img (getHeight)))  ;;fill the specified RECTANGLE
      )  ;;So, after this first modification pipeline via doto, to bg; we have a GREAT WHITE RECTANGLE (our world)
    (dorun 
     (for [x (range dim) y (range dim)]
       (render-place bg (v (+ (* x dim) y)) x y)))  ;;then the index of each place will be applied in order to get the value of that place
    (doto bg
      (.setColor (. Color blue))
      (.drawRect (* scale home-off) (* scale home-off) 
                 (* scale nants-sqrt) (* scale nants-sqrt)))
    (. g (drawImage img 0 0 nil))
    (. bg (dispose))))

;;***************** FOR INSPECTION ***********************
(def img (new BufferedImage (* scale dim) (* scale dim) 
                 (. BufferedImage TYPE_INT_ARGB)))  ;;BufferedImage(int width, int height, int imageType) Constructs a BufferedImage of one of the predefined image types. The ARGB model adds a fourth number representing Opacity, or the Alpha-Channel (0 -> Full transparence, 255 -> Fully opaque)

(def bg  (. img
            (getGraphics)))  ;;this value will be threated to fill-cell. getGraphics() returns a Graphics2D, which can be used to draw into this image. Every Graphics2D object is associated with a target that defnies where rendering takes place, the same rendering target is used throughout the life of a Graphics2D object. I think of it like a CANVAS (just like FRAME and PANEL) representing *something* else (actually I do not understand it fully)

(instance? java.awt.Graphics2D bg)  ;;the bg variable in render function is a Graphics2D instance

(.setColor bg (. Color white))
;;-->  #object[sun.java2d.SunGraphics2D 0x7d7cf10f "sun.java2d.SunGraphics2D[font=java.awt.Font[family=Dialog,name=Dialog,style=plain,size=12],color=java.awt.Color[r=255,g=255,b=255]]"]
(.setColor bg (. Color blue))
;;As you can see, there is a variable that changed with these operations
;;********************************************************


(def panel (doto (proxy [JPanel] []
                        (paint [g] (render g)))
             (.setPreferredSize (new Dimension 
                                     (* scale dim) 
                                     (* scale dim)))))
(def frame (doto (new JFrame) (.add panel) .pack .show))

;;this is gonna be incharge of the timing of the whole simulation
(def animator (agent nil))
(defn animation [x]
  (when running
    (send-off *agent* #'animation))  ;;this recursively character is going to create an infinite delivery of the function animation. As send-off must inmediately return a value, the following instructions are gonna be made first, and then the next call to animation, that actually is the same. 
  (. panel (repaint))  ;;Normally this method calls first an update() method and then paint(). We can only see the repaint() call.
  (. Thread (sleep animation-sleep-ms))  ;;here comes the freeze effect.
  nil)  ;;and this is the returned value to the previous send-off call (the very first one returns nil even before executing a thing, this creates a COMPLETELY INDEPENDENT PROCESS OF RENDERING and the AGENT ANIMATOR manages it)

;;then the same concept is used for the evaporation proccess of the pheromones. The question is how these TWO INDEPENDT process manage to present us a COORDINATED RENDERING PROCESS.
(def evaporator (agent nil))
(defn evaporation [x]
  (when running
    (send-off *agent* #'evaporation))
  (evaporate)
  (. Thread (sleep evap-sleep-ms))
  nil)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; use ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(comment
;demo
  (load-file "/Users/rich/dev/clojure/ants.clj")
  
  (def ants (setup))  ;;ALTER to REF's associating FOOD and ANT struct. 

  (let [coords (map (fn [a] @a) ants)]
    [(first coords) (last coords)])
  ;;In fact, the ants are in 20-26 range (in the house) -> 49 ants in total

  (def actual-food-place (map (fn [p] @p) (filter (fn [p] (> (:food @p) 0))  (flatten world))))
  (count actual-food-place)
  ;;there are less than 35 places with more food than 0
  (def test (atom 0))
  (dotimes [i 2]
    (swap! test inc))
  @test
  ;;that means that (rant-int food-range) was one time 0

  ;;in BEHAVE fn, in the second COND representing "I DO NOT HAVE FOOD (foraging)", the first condition says that the food must be greater than 0, but also that that place is not home. When distributing the food-places, it was a completely random distribution and it could be that a HOME place was filled with food.
  (filter (fn [p] (:home p)) actual-food-place)
  
  ;;ANIMATOR, EVAPORATOR and EACH ONE OF THE ANTS are INDEPENDENT PROCESS managed by an agent
  (send-off animator animation)  
  (dorun (map #(send-off % behave) ants))
  (send-off evaporator evaporation)

  )
