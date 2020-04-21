(ns Algorithms.Laplaceexperiment)

;;Das Problem war: Ein Lego-Achter (bzw. ein Laplace-Wuerfel) wird mehrmals geworfen. Wenn man beim ersten Wurf die 1 erhaelt, muss man abbrechen [...] Man ist "duchgekommen" wenn man auch beim 6. Wurf keine 6 erwischt. A)Berechnen Sie die Wahrcheinlichkeit fuer ein Durchkommen bei einem Lego-Achter. B) Fuhren sie das Experiment mit dem Lego Ahcter je 50-mal durch, Wie oft sind Sie durchgekommen? Vergleichen Sie mit der berechneten Wahrscheinlichkeit. C) Berechnen Sie die Wahrscheinlichkeitsverteilung fuer die Versuchsdauer, die zwischen 1 und 6 Wuerfen liegen muss -Ueberpruefen Sie, ob deren Erwartungswert in der Mitte bei 3,5 liegt. 

(def Ergebnissmenge (range 1 7))  ;;Ein Wuerfel hat nur 6 moegliche Ergebnisse

(defn W-werfen []
  (rand-nth Ergebnissmenge))  ;;alle Moeglichkeiten haben dasselbe Wahrscheinlichkeit, deswegen ist ganz zufaellig

(defn spielen []
  (loop [turn 1
        history []
        ergebniss (W-werfen)]
    (cond
      (= ergebniss turn) {:lost (conj history ergebniss)} ;;wenn dein Ergebniss gleich der Zahl von shon gemachte Wuerfe ist ("Wenn man beim ersten Wurf die 1 erhaelt, muss man abbrechen"), dann verliert man
      (= turn 7) {:won history} ;;mehr als 6 Wuerfe darf man nicht machen. Und wenn man bis zu dem sechsten Wuerf angekommen ist, dann hat automatisch gewonnen.
      :else (recur (+ turn 1)
                   (conj history ergebniss)
                   (W-werfen)))))

(defn Spielen [x]
  (for [i (range x)] (spielen)))  ;;dann wir das Process "spielen" automatiziert fuer "x" spielen

(defn Beschreibung  ;;diese Funktion ergibt uns eine genaue Beschreibung von einer Menge von Spielen, je nach welche Bedingung wir versuchen wollen
  [games Bedingung]
  (let [dazugehoerige-games (apply vector
                        (remove (fn [val] (= val nil))
                                (map (fn [game] (Bedingung game)) games)))
        percentage (/ (count dazugehoerige-games) (count games))]
    {percentage dazugehoerige-games}))  ;;nur diese 2 Daten brauchen wir: Einmal wie viele Spielen die Bedingung treffen (percentage oder Wahrscheinlichkeit) und die Spielen an sich selbst um weiter zu analisieren!

(defn beschreiben [games]  ;;die lezte Funktion wird benutzt um nach zwei Bedingungen zu untersuchen
  (let [describe (partial Beschreibung games)]
    {:gewonen (describe :won)  ;;die gewonnene Spielen
     :verloren (describe :lost)}))  ;;und die verlorene Spielen

(beschreiben (Spielen 10))  ;;wuerde so aussehen:
{:gewonen {3/5 [[1 0 6 2 0 1 1] [1 0 5 5 3 6 3] [1 5 6 6 6 2 0] [1 2 4 4 2 3 1] [1 6 4 1 0 3 3] [1 3 4 2 5 2 3]]},
 :verloren {2/5 [[1 4] [1] [1 0 0 0 3 1] [1 2 4]]}}

(defn expt [b x]  ;;ich musste das exponentielle Funktion von null aus definieren, sonst haette ich die aus Internet runtergeladet.
  (apply * (for [i (range x)] b)))
(defn to-prctg [x]  ;;this function just converts any ratio into percentage value
  (Math/round (* (float x) 100)))
(defn average [coll]
  (float (/ (reduce + coll) (count coll))))

 ;;A) Wenn es klar ist dass beim Wurf X nicht das X kommen darf um wir weiter gehen zu koennen, dann ist diese Wahrcheinlichkeit 33%
(def theoretische-Wert (to-prctg (expt (/ 5 6) 6)))

;;B) Die Funktion "tell-difference" berechnet die die Differenz inzwischen unsere theorische Wert und der Wert, der wir nach n-games kriegen. Daher koennen wir sehen das diese zwei Werte nicht exakt dieselbe sein werden, aber werden immer nacheinander naeher sein.
(defn tell-difference [n-games prezision]
  (letfn [(compare-percentage [n]  ;;im vergleich zu den "theoretische-Wert"
            (-> (first (keys (:gewonen (beschreiben (Spielen n)))))
                (to-prctg)
                (- theoretische-Wert)
                (Math/abs)))  ;;bis hier haben wir der           
          (determine-progressively [p]  ;;je mehrmals wir das Experiment fuehren, desto naeher werden wir zu diesem "theoretische-Wert" (hier fuegen wir ein das Konzept von Praezision)
            (average (take p (map (fn [_] (compare-percentage n-games)) (range)))))]
    (determine-progressively prezision))) 

(map (fn [func] (func (map (fn [p] (tell-difference 50 p)) (range 1 100)))) [first last])

;;C) Das Erwartungswert fuer ein Wurf liegt ungefaehr bei 2, nicht beim 3,5 (nach der gegebene Funktion).
(defn Erwartungswert-von-Versuchdauer [n]
  (let [Versuchsdauer (map (fn [coll] (last coll)) (first (vals (:verloren (beschreiben (Spielen n))))))
        f (frequencies Versuchsdauer)]
    (float (reduce + (map (fn [a b] (* a (/ b n))) (keys f) (vals f))))))
(Erwartungswert-von-Versuchdauer 100)
