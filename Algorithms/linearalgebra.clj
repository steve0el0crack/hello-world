(ns Algorithms.linearalgebra)

;;I will be working on Linear Algebra with MIT Online Course Linear Algebra (https://ocw.mit.edu/courses/mathematics/18-06-linear-algebra-spring-2010/assignments/) and "Introduction to lineal algebra" by Gilbert Strang

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

;;Problem Set 1.1 7
(let [vars '(0 1 2)]
  (for [a vars b vars]
    (linear-combination [a b] '([2 1] [0 1]))))

;;the next question is how, given a collection of vectors, to determine if they are on the same plane or not.
(defn same-plane?
  [coll]
  ())




