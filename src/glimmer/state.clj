(ns glimmer.state
  (:require [clojure.string :as string]))

(def state (atom
            {:mode            :normal
             :lines           []
             :cursor          {:x 0 :y 0}
             :operation-stack []
             :bar-status      {}
             :size            [0 0]}))

(defn get-state [& keys]
  (get-in @state (vec (flatten keys))))

(defn update-state [key val]
  (swap! state assoc key val))

(defn update-in-state [keys fn]
  (swap! state update-in keys fn))

(defn update-and-get [key val]
  (get (update-state key val) key))

(defn merge-state [map]
  (merge map @state))

(defn reset-state [map]
  (for [k (keys map)]
    (update-state k (get map k)))
  @state)

(defn state-map []
  @state)

(defn bar-section [text k]
  (let [prev (get-state :bar-status k)]
    (println text k prev)
    (when (not= prev text)
      (println @state)
      (update-in-state [:bar-status] #(merge % {k text})))))
