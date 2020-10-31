(ns clojure-progress.incanter)

(use '(incanter core stats charts io))

(view (histogram (sample-normal 1000)))


