(ns Algorithms.linearalgebra)

;;we first define how to do a simple linear combination
(defn vector
  [dims]
  (into [] (map (fn [_] (rand-int 3)) (range dims))))

(def sample-vector (map (fn [_] (vector 3)) (range 3)))

(defn linear-combination
  [vars vectors]
  (apply map (fn [& vcs]  ;;using Clojure VARIADIC property of fn's
               (reduce +
                       (map #(* %1 %2) vcs vars)))
         vectors))

(linear-combination [1 2 3] sample-vector)

;;the next question is how, given a collection of vectors, to determine if they are on the same plane or not.
(defn same-plane?
  [coll]
  ())




