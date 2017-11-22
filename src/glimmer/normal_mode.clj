(ns glimmer.normal-mode
  (:require [glimmer.state :as state]
            [glimmer.functions :as f]))

(defn pop-op-stack!
  [state stack]
  (let [{x :x y :y :as cursor} (:cursor state)
        head                   (first stack)
        tail                   (vec (rest stack))]
    (state/update-state :operation-stack tail)
    (cond
      (= head :quit) nil
      :else          state)))

(defn process-normal-mode [key {x     :x     y      :y      stack  :operation-stack
                                lines :lines cursor :cursor status :bar-status mode :mode
                                :as   state}]
  (cond
    (= \i key) (if (= mode :normal) (state/update-state :mode :edit) state)
    (= \c key) (state/update-state :operation-stack [])
    (= \w key) (f/process-up cursor lines)
    (= \a key) (f/process-left cursor lines)
    (= \s key) (f/process-down cursor lines)
    (= \d key) (f/process-right cursor lines)
    (= \: key) (state/update-state :mode :interpret)
    :else      state))
