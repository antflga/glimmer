(ns glimmer.insert-mode
  (:require [glimmer.state :as state]
            [glimmer.functions :as f]))

(defn process-backspace
  [{x :x y :y :as cursor} lines]
  (let [is-line-start   (= 0 x)
        is-first-line   (= 0 y)
        should-collapse (and is-line-start (not is-first-line))]
    (if should-collapse
      (let [current-line  (nth lines y)
            previous-line (nth lines (dec y))
            merged-line   (clojure.string/join "" [previous-line current-line])
            new-lines     (assoc lines y [merged-line])
            new-lines     (assoc new-lines (dec y) [])
            new-lines     (into [] (flatten new-lines))
            new-cursor    {:x (count previous-line) :y (dec (:y cursor))}]
        (state/update-state :lines new-lines)
        (state/update-state :cursor new-cursor))
      ;; Else, we just remove a single character
      (let [current-line (nth lines y)
            new-line     (f/remove-char current-line x)
            new-lines    (assoc lines y new-line)
            new-cursor   (assoc cursor :x (- (:x cursor) 1))
            new-cursor   (f/clamp-cursor new-cursor lines)]
        (state/update-state :lines new-lines)
        (state/update-state :cursor new-cursor)))))

(defn process-char
  "Inserts a character at the cursor position, moves the cursor forward
  `x` `y` from `cursor` and `lines` as lines of file, `char` is character"
  [{x :x y :y :as cursor} lines char]
  (let [line-to-modify (nth lines y)
        modified-line  (f/str-insert line-to-modify char x)
        new-lines      (assoc lines y modified-line)
        new-cursor     {:x (inc x) :y y}]
    (state/update-state :lines new-lines)
    (state/update-state :cursor new-cursor)))

(defn process-enter
  [{x :x y :y :as cursor} lines]
  (let [current-line           (nth lines y)
        replaced-line-contents (subs current-line 0 x)
        next-lines-contents    (subs current-line x)
        new-lines              (assoc lines y replaced-line-contents)
        [before, after]        (split-at (inc y) new-lines)
        new-lines              (into [] (concat before [next-lines-contents] after))
        new-cursor             {:x 0 :y (inc y)}]
    (state/update-state :lines new-lines)
    (state/update-state :cursor new-cursor)))

(defn process-insert-mode [key {x     :x     y      :y      stack  :operation-stack
                                lines :lines cursor :cursor status :bar-status mode :mode
                                :as   state}]
  (cond
    (= :backspace key)                            (process-backspace cursor lines)
    (= :enter key)                                (process-enter cursor lines)
    (= java.lang.Character (type key))            (process-char cursor lines key)
    :else                                         state))
