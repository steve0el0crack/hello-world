#lang racket

;; The native Scheme or MIT Scheme language is a little bit difficult to use due to the environment setup details.
;; Therefore I am starting to dive into the book "SICP" (Structure and Interpretation of Computer Programs) with DrRacket.
;; There should not be a great difference...

;; Exercise 1.6 page 25: Implementation and manipulation of Newton's method of successive aproximations.
;; "whenever we have a guess y for the value of the square root of a number x, we can perform a simple manipulation to get a better guess (one closer to the actual square root) by averaging y with x/y" 
 
(define (step a x)
  (if (< (abs (- (* a a) x)) 0.0001)
      a
      (newton-sqrt (/ (+ a (/ x a)) 2) x)))
(define (newton-sqrt x) (step 1 x))