(ns glimmer.glimmer
  (:require [glimmer.state :as state]
            [glimmer.insert-mode :as insert]
            [glimmer.normal-mode :as normal]
            [glimmer.interpret-mode :as interp]
            [glimmer.functions :as f]))

(defn delegate-mode-handler [key state]
  (cond (= (:mode state) :edit)      (insert/process-insert-mode key state)
        (= (:mode state) :normal)    (normal/process-normal-mode key state)
        (= (:mode state) :interpret) (interp/process-interpret-mode key state)
        :else                        state))

(defn process-key
  "Processes one input character from the terminal.
  Returns the new state. "
  [key {cursor :cursor lines :lines :as state}]
  (cond
    (and (= key :escape) (not= (:mode state) :normal)) (state/update-state :mode :normal)
    (= :left key)  (f/process-left cursor lines)
    (= :right key) (f/process-right cursor lines)
    (= :up key)    (f/process-up cursor lines)
    (= :down key)  (f/process-down cursor lines)
    :else          (delegate-mode-handler key state)))
