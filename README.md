# glimmer

A Clojure library designed to ... well, that part is up to you.

## Usage

FIXME

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.



;; (defn process-char
;;   " Inserts a character at the cursor position, moves the cursor forward "
;;   [char state]
;;   (let [lines          (:lines state)
;;         cursor         (:cursor state)
;;         line-to-modify (nth lines (:y cursor))
;;         modified-line  (str-insert line-to-modify char (:x cursor))
;;         new-lines      (assoc lines (:y cursor) modified-line)
;;         new-cursor     (assoc cursor :x (+ (:x cursor) 1))]
;;     (merge state {:lines new-lines :cursor new-cursor})))

;; (defn clamp [nmin nmax n] (if (< n nmin) nmin (if (> n nmax) nmax n)))

;; (defn clamp-cursor
;;   [cursor lines]
;;   (let [y            (:y cursor)
;;         x            (:x cursor)
;;         clamped-y    (clamp 0 (dec (count lines)) y)
;;         current-line (get lines clamped-y "")
;;         clamped-x    (clamp 0 (count current-line) x)]
;;     {:x clamped-x :y clamped-y}))

;; (defn process-down
;;   [_ state]
;;   (let [lines      (:lines state)
;;         cursor     (:cursor state)
;;         new-cursor (assoc cursor :y (inc (:y cursor)))
;;         new-cursor (clamp-cursor new-cursor lines)]
;;     (merge state {:lines lines :cursor new-cursor})))

;; (defn process-up
;;   [_ state]
;;   (let [lines      (:lines state)
;;         cursor     (:cursor state)
;;         new-cursor (assoc cursor :y (dec (:y cursor)))
;;         new-cursor (clamp-cursor new-cursor lines)]
;;     (merge state {:lines lines :cursor new-cursor})))

;; (defn process-right
;;   [_ state]
;;   (let [lines      (:lines state)
;;         cursor     (:cursor state)
;;         new-cursor (assoc cursor :x (inc (:x cursor)))
;;         new-cursor (clamp-cursor new-cursor lines)]
;;     (merge state {:lines lines :cursor new-cursor})))

;; (defn remove-char
;;   [string pos]
;;   (if (= pos 0)
;;     string
;;     (str (subs string 0 (dec pos)) (subs string pos))))

;; (defn process-backspace
;;   [_ state]
;;   (let [lines           (:lines state)
;;         cursor          (:cursor state)
;;         y               (:y cursor)
;;         x               (:x cursor)
;;         is-line-start   (= 0 x)
;;         is-first-line   (= 0 y)
;;         should-collapse (and is-line-start (not is-first-line))]
;;     (println y)
;;     (if should-collapse
;;       (let [current-line  (nth lines y)
;;             previous-line (nth lines (dec y))
;;             merged-line   (clojure.string/join "" [previous-line current-line])
;;             new-lines     (assoc lines y [merged-line])
;;             new-lines     (assoc new-lines (dec y) [])
;;             new-lines     (into [] (flatten new-lines))
;;             new-cursor    {:x (count previous-line) :y (dec (:y cursor))}]
;;         (merge state {:lines new-lines :cursor new-cursor}))
;;                                         ; Else, we just remove a single character
;;       (let [current-line (nth lines y)
;;             new-line     (remove-char current-line x)
;;             new-lines    (assoc lines y new-line)
;;             new-cursor   (assoc cursor :x (- (:x cursor) 1))
;;             new-cursor   (clamp-cursor new-cursor lines)]
;;         (merge state {:lines new-lines :cursor new-cursor})))))

;; (defn process-enter
;;   [_ state]
;;   (let [lines                  (:lines state)
;;         cursor                 (:cursor state)
;;         current-line           (nth lines (:y cursor))
;;         replaced-line-contents (subs current-line 0 (:x cursor))
;;         next-lines-contents    (subs current-line (:x cursor))
;;         new-lines              (assoc lines (:y cursor) replaced-line-contents)
;;         [before, after]        (split-at (inc (:y cursor)) new-lines)
;;         new-lines              (into [] (concat before [next-lines-contents] after))
;;         new-cursor             {:x 0 :y (inc (:y cursor))}]
;;     (merge state {:lines new-lines :cursor new-cursor})))

;; (defn pop-op-stack [state]
;;   (let [stack (:operation-stack state)
;;         head  (first stack)
;;         tail  (rest stack)]
;;     (cond
;;       (= head :quit) nil
;;       :else          state)))

;; (defn process-normal-mode [key state]
;;   (cond
;;     (and (= (str key) "i") (= (:mode state) :normal)) (merge state {:mode :edit})
;;     (= "c" (str key))                                 (assoc state :operation-stack [])
;;     (= "q" (str key))                                 (update-in state [:operation-stack] conj :quit)
;;     (= :enter key)                                    (pop-op-stack state)
;;     :else                                             state))

;; (defn process-insert-mode [key state]
;;   (cond
;;     (= :backspace key)                            (process-backspace key state)
;;     (= :enter key)                                (process-enter key state)
;;     (and (= key :escape) (= (:mode state) :edit)) (assoc state :mode :normal)
;;     (= java.lang.Character (type key))            (process-char key state)
;;     :else                                         state))

;; (defn pls [key state]
;;   (cond (= (:mode state) :edit)   (process-insert-mode key state)
;;         (= (:mode state) :normal) (process-normal-mode key state)
;;         :else                     state))

;; (defn process-left
;;   [_ state]
;;   (let [lines      (:lines state)
;;         cursor     (:cursor state)
;;         new-cursor (assoc cursor :x (dec (:x cursor)))
;;         new-cursor (clamp-cursor new-cursor lines)]
;;     (merge state {:lines lines :cursor new-cursor})))

;; (defn process-key
;;   "Processes one input character from the terminal.
;;   Returns the new state. "
;;   [key state]
;;   (cond
;;     (= :left key)  (process-left key state)
;;     (= :right key) (process-right key state)
;;     (= :up key)    (process-up key state)
;;     (= :down key)  (process-down key state)
;;     :else          (pls key state)))
