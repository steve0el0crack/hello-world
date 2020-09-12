(ns hello-world.yassin)

;; PROBLEM 1

(defn eval  ;; falls x durch zwei teilbar ist und diese zahl wiederum durch zwei teilbar ist wir YES als Antwort ausgespuckt sonst NEIN
  [x]
  (if (= (rem x 2) 0)
    (if (= (rem (/ x 2) 2) 0)
      "YES")
    "NEIN"))

(eval (rand-int 101))


;; PROBLEM 2

