;;here I am gonna develope how to solve a rubik's cube, while learning ANSI Common Lisp, with Paul Graham.

;;this implementation comes from RosettaCode
(defun flatten (structure)
  (cond ((null structure) nil)
        ((atom structure) (list structure))
        (t (mapcan #'flatten structure))))  ;;I do not fully understand how this MAPCAN functions...

(trace flatten)
(flatten '(a b (c d) (x y))) ;;but with the use of trace and untrace funct's it is much more clar
(untrace flatten)

(setf solved-face '((1 1 1) (1 1 1) (1 1 1)))  ;;may also been defvar

(defun get-first-file (face)
  (mapcar #'first face))  ;;#' is a way for resolving to an object in itself, in this case a function. mapCAR... first (cons cells)

(defun is-solved? (face)
  (= 9 (reduce #'+ (flatten face))))

(is-solved? solved-face)
