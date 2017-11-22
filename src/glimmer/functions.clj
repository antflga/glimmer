(ns glimmer.functions
  (:require [glimmer.state :as state]))

(defn clamp [nmin nmax n] (if (< n nmin) nmin (if (> n nmax) nmax n)))

(defn clamp-cursor
  [{x :x y :y :as cursor} lines]
  (let [clamped-y    (clamp 0 (dec (count lines)) y)
        current-line (get lines clamped-y "")
        clamped-x    (clamp 0 (count current-line) x)]
    {:x clamped-x :y clamped-y}))

(defn process-down
  [{y :y :as cursor} lines]
  (if (= (inc y) (count lines))
    (let [lines (:lines (state/update-state :lines (conj lines "")))]
      (process-down cursor lines))
    (let [new-cursor (assoc cursor :y (inc y))
          new-cursor (clamp-cursor new-cursor lines)]
      (state/update-state :cursor new-cursor))))

(defn process-up
  [{y :y :as cursor} lines]
  (let [new-cursor (assoc cursor :y (dec y))
        new-cursor (clamp-cursor new-cursor lines)]
    (state/update-state :cursor new-cursor)))

(defn process-right
  [{x :x y :y :as cursor} lines]
  (state/bar-section (str "len: " (count (nth lines y))) :meme)
  (if (= x (count (nth lines y)))
    (let [lines (update-in lines (vector y) #(str % " "))]
      (process-right cursor (state/update-and-get :lines lines)))
    (let [new-cursor (assoc cursor :x (inc x))
          new-cursor (clamp-cursor new-cursor lines)]
      (state/update-state :cursor new-cursor))))

(defn process-left
  [{x :x y :y :as cursor} lines]
  (let [new-cursor (assoc cursor :x (dec (:x cursor)))
        new-cursor (clamp-cursor new-cursor lines)]
    (state/update-state :cursor new-cursor)))

(defn remove-char
  [string pos]
  (if (= pos 0)
    string
    (str (subs string 0 (dec pos)) (subs string pos))))

(defn str-insert
  "Insert char in string at index i."
  [string char i]
  (str (subs string 0 i) char (subs string i)))
