#lang racket

;; The native Scheme or MIT Scheme language is a little bit difficult to use due to the environment setup details.
;; Therefore I am starting to dive into the book "SICP" (Structure and Interpretation of Computer Programs) with DrRacket.
;; There should not be a great difference...

;; Exercise 1.6 page 25: Implementation and manipulation of Newton's method of successive aproximations.
;; "whenever we have a guess Y for the value of the square root of a number X, we can perform a simple manipulation
;; to get a better guess (one closer to the actual square root) by averaging Y with X/Y" 

(define (^-step x a r)
  (if (= a 0)
      r
      (^-step x
         (- a 1)
         (* x r))))

(define (^ x a) (^-step x a 1))

(define (step-0 guess x)  ;; this process will be made internally and therefore it is a STEP...
  (if (< (abs (- (^ guess 2) x))
         0.0001)  ;; we will never get to the exact square root, but very close!
      (if (> guess 1) 
          (round guess)  ;; for numbers greater than 1, the unit will be enough.
          guess)  ;; but in the case of 0.25, I want 0.5 as answer... not rounded.
      (step-0 (/ (+ guess (/ x guess)) 2)
            x)))

(define (newton-sqrt-0 x) (step-0 100 x))  ;; 1 is only a matter of simplicity. It could be any other number.

;; Exercise 1.7 page 25: The last implementation of the algorithm uses a specific test for deciding when to stop the loops.
;; If the result (* GUESS GUESS) differs by no more than 0.0001 from the actual asked number X, then it is a good enough result.
;; this time another method will be implemented... the rate of change of GUESS.

(define (step-1 guess x rate-of-change)  ;; the concept of RATE-OF-CHANGE is introduced.
  (if (< rate-of-change 0.000000001)  
      (if (> guess 1) 
          (round guess)  
          guess) 
      (step-1 (/ (+ guess (/ x guess)) 2)
              x
              (abs (- (/ (+ guess (/ x guess)) 2) guess)))))  ;; when the sqrt's of numbers lower than 1 is asked, then the guess becomes lower than 1 too, and this substraction ends up negative!

(define (newton-sqrt-1 x) (step-1 1 x 1))

;; Exercise 1.8 page 26: There is a formula for cube roots too! Implement it...
;; (X/Y^2 + 2Y)/3

(define (step-cube guess x)  ;; I did'nt notice much of an advantage when adding the variable RATE-OF-CHANGE. There I am leaving it on the side...
  (if (< (abs (- (^ guess 3) x))
         0.0001)  
      (if (> guess 1) 
          (round guess)  
          guess)  
      (step-cube (/ (+ (* 2 guess)
                    (/ x (^ guess 2)))
                 3)
            x)))

(define (cube x) (step-cube 1 x))

;; Challenge Problem page. 40: Counting change
;; Given any amount and any list/set containing any type of coins, how many ways of changing the amount given with the coins given are there?
;; The implementation given in the book is the following...

(define (count-change amount)
  (cc amount 5))

;; Just by the way, the concept being learned is that of ITERATIVE and RECURSIVE process.
;; In this case, the process will evolve (expand) into a large expresion waiting for one of the two expresions/conditions defined above; and then collapse into one unique primitive... the returning value of the function.
;;; And that is a recursive process. But this particular process does not evolve linearly, but exponentially since there are two expansions at each level. So, the evolution actually seems like a tree.

(define (cc amount kinds-of-coins)
  (cond
    ((= amount 0) 1)  ;; this S-exp resolves to the primitve 1
    ((or (< amount  0) (= kinds-of-coins 0)) 0)  ;; this S-exp resolves to the primitive 0
    (else (+ (cc amount
                 (- kinds-of-coins 1)) ;; this expresion will expand
             (cc (- amount
                    (first-denomination kinds-of-coins))
                 kinds-of-coins)  ;; and this too
             ))))

(define (first-denomination kinds-of-coins)
  (cond ((= kinds-of-coins 1) 1)
        ((= kinds-of-coins 2) 5)
        ((= kinds-of-coins 3) 10)
        ((= kinds-of-coins 4) 25)
        ((= kinds-of-coins 5) 50)))

;; In this tree (like in normal-order-evaluation), evaluations will be unnecessarily repeated... is it possible to prevent this? The book states that one answer is memoization/tabulation.
;; This consists of building a table at compilation time, in which the values previously calculated will be stored for next evalutions, so that the next time it will a matter of searching.
;; But the question still remains: Is there an implementation/definition that solves this issue the most higyenic way?

;; Exercise 1.11 page 42:
;; A function is defined by the rule that f(x) = n if n < 3 and f(n) = f(n - 1) + 2*f(n - 2) + 3*f(n - 3) when n > or = 3.
;; Write a procedure that computes f by means of a recursive process, and another by means of an iterative processs.

(define (my-func n)  ;; RECURSIVE
  (if (< n 3)
      n
      (+ (my-func (- n 1))
         (* 2 (my-func (- n 2)))
         (* 3 (my-func (- n 3))))))

(define (my-func-iter n)
  (iter 0 1 2 2 n))  ;; the first three computations as state variables (0 1 2) and from then on, the rest can be easily calculated on each iteration separately.

(define (iter a b c counter max-iter)
  (if (= counter max-iter)
      c  
      (if (< max-iter 3)
          max-iter
          (iter b 
                c 
                (+ c
                   (* b 2)
                   (* a 3)) ;; for the next iteration I am leaving the result of (n - 3) apart and including a new value (n)
                (+ counter 1)  ;; this first iteration means that n was not 0, nor 1, nor 2, or any other negative number. Therefore it would count as if three iterations were already made!çc
                max-iter))))

(define (test n)  ;; and finally a create a test that proves the functionality and similarity of the two functions.
  (define (step n) (= (my-func n) (my-func-iter n)))
  (map step (range n)))

;; Exercise 1.12 page 42:
;; Programming Pascal's triangle (+- normal distribution) by means of a recursive process.
;; Knowing that the edges are always 1, and each number is the sum of the two above:
;;
;;                    1
;;                  1   1
;;                1   2   1
;;              1   3   3   1
;;            1   4   6   4   1

;; Here I found a problem with the statement: Am I suposse to construct the complete triangle from a single recursive process?
;; In that case I couldn't solve the problem and maybe in the future with some more experience (after reading Knuth or a little bit more of mathematic) I would be able to come up with something!
;; But in order to apply the concepts explained in the chapter and make sense of this exercise, I searched for another approach to this problem in internet: Finding 1 number regarding its position in an imaginary "table"
;;
;; https://stackoverflow.com/questions/409784/whats-the-simplest-way-to-print-a-java-array
;;              0  1  2  3  4
;; Row null     1 
;; Row one      1  1 
;; Row two      1  2  1
;; Row three    1  3  3  1
;; Row four     1  4  6  4  1  ...
;;
;; In this system, I certainly could be able to say (pascal x y) and get the corresponding number.

;; As usual, in a recursive process there is going to be an expression that will expand till some conditions were to be met and then, a process of compresion follows.
(define (pascal x y)
  (cond
    ((or (< x 0) (< y 0)) 0)  ;; my first condition means that if I want to calculate for example (pascal 0 4), then I would need (+ (pascal 0 3) (pascal -1 3)) and an infinite loop will be initiated till -infinity...
    ((and (= x 0) (= y 0)) 1)  ;; this is much like my construction block, since every number will be ultimately just a sum of 1's.
    (else (+
           (pascal x (- y 1))  ;; the element direct above the number I am searching for.
           (pascal (- x 1) (- y 1))  ;; the other summand.
           ))  ;; here occurs the recusiveness that actually creates a tree-like process in which the number of divions follows the 2^n growth.
    ))





















