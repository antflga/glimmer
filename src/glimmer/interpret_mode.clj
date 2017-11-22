(ns glimmer.interpret-mode
  (:require [glimmer.state :as state]))


(defn pop-op-stack!
  [state stack]
  (let [{x :x y :y :as cursor} (:cursor state)
        head                   (first stack)
        tail                   (vec (rest stack))
        stack                  (state/update-and-get :operation-stack tail)
        state                  (state/state-map)]
    (cond
      (= head :quit)                       nil
      (integer? (read-string (name head))) (dotimes [n (int (name head))]
                                             (pop-op-stack! state stack))
      (= head :kill-char ())               (print "lol")
      :else                                state)))

(defn key-is-int? [key]
  (if-not (keyword? key)
    false
    (-> key
        name
        read-string
        integer?)))

(defn push-op-stack! [instr]
  (let [stack        (state/get-state :operation-stack)
        head-is-int? (key-is-int? (first stack))
        inst-is-int? (key-is-int? instr)
        both?        (and head-is-int? inst-is-int?)]
    (if both?
      (let [newinstr (keyword (reduce str (map name [(first stack) instr])))
            newstack (conj  (rest stack) newinstr)]
        (state/update-state :operation-stack newstack))
      (state/update-in-state [:operation-stack] #(conj % instr)))))

(defn process-interpret-mode [key {x     :x     y      :y      stack  :operation-stack
                                   lines :lines cursor :cursor status :bar-status mode :mode
                                   :as   state}]
  (cond
    (= :enter key)                     (pop-op-stack! state stack)
    (= \q key)                         (push-op-stack! :quit)
    (= \x key)                         (push-op-stack! :kill-char)
    (integer? (read-string (str key))) (push-op-stack! (keyword (str key)))
    :else                              state))
