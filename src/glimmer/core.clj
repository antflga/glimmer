(ns glimmer.core
  (:gen-class)
  (:require [glimmer.glimmer :as glimmer]
            [glimmer.state :as state]
            [lanterna.terminal :as t]
            [clojure.core.async :as a]
            [clojure.string :as string]
            [clojure.string :as str]))

(defn interval [f msecs]
  (let [action (a/chan (a/dropping-buffer 1))
        timing (a/chan)
        kickoff
        #(a/go
           (a/<! (a/timeout msecs))
           (a/>! timing true))]
    (a/go-loop []
      (when (a/<! action)
        (f)
        (recur)))
    (a/go-loop []
      (if (a/<! timing)
        (do
          (a/>! action true)
          (kickoff)
          (recur))
        (a/close! action)))
    (kickoff)
    #(a/close! timing)))

(defn handle-resize [cols rows]
  (dosync (state/update-state :size [cols rows])))

(defn n-strs [n string] (reduce str (repeat n string)))

(defn fw-str
  ([width fill-str before string after]
   (let [len      (count string)
         fill-len (count fill-str)
         pad      (apply + (map count [before after]))
         distance (- width (+ len pad))
         distance (/ distance fill-len)
         fill     (n-strs distance fill-str)]
     (str before string fill after)))
  ([width fill-str string]
   (fw-str width fill-str "" string "")))

(defn positions
  [pred coll]
  (keep-indexed (fn [idx x]
                  (when (pred x)
                    idx))
                (map str (vec coll))))

(defn rep-str
  "insert c in string s at index i."
  [s c i]
  (let [f (subs s 0 i)
        l (subs s (+ i 1))]
    (str f c l)))

(defn match-chars [in-str char-match out-str char-replace]
  (let [indices (fn [s] (positions #{char-match} s))]
    (loop [s out-str
           i (indices in-str)]
      (println {:str s :i i})
      (if-not (empty? i)
        (recur (rep-str s char-replace (first i)) (rest i))
        s))))

(defn render
  "Renders lines and cursor to the terminal"
  [term state]
  (let [size (:size state)]
    (when (= 0 (second size)) (state/update-state :size (t/get-size term)))
   ;;
    (let [top-left    "┌"   top-right    "┐"
          left        "├→ " right       " │"
          bottom-left "└"   bottom-right "┘"
          separator   "│"   up           "┴"
          down        "┬"   line         "─"
          x           (first size)
          y           (- (second size) 1)
          bar         (str (string/join (str " " separator " ") (vals (:bar-status state))))
          bar         (fw-str x " " left bar right)
          bottom-str  (reduce str (repeat (- (count bar) 2) line))
          top-str     (fw-str x line top-left bottom-str top-right)
          bottom-str  (fw-str x line bottom-left bottom-str bottom-right)
          bottom-str  (match-chars bar (string/trim separator) bottom-str up)
          top-str     (match-chars bottom-str up top-str down)]
      (t/clear term)
      (t/put-string term (clojure.string/join "\n" (concat (:lines state) state)))
      (t/put-string term (str (apply str (drop-last top-str)) top-right) 0 (- y 2))
      (t/put-string term bar 0 (- y 1))
      (t/put-string term  (str (apply str (drop-last bottom-str)) bottom-right) 0 y)
      (t/move-cursor term (:x (:cursor state)) (:y (:cursor state))))))

(defn -get-lines
  "Returns an array of lines from a file, given a file path"
  [file-path]
  (clojure.string/split-lines (slurp file-path)))

(defn editor-loop
  [term state]
  (loop [term term state state]
    (let [key (t/get-key-blocking term)]
      (let [next-state (glimmer/process-key key state)]
        (when-not (nil? next-state)
          (recur term next-state))))))

(defn tick [term state]
  (let [{x :x y :y :as cursor} (state/get-state :cursor)
        mode                   (state/get-state :mode)]

    (state/bar-section (str (state/get-state :mode) "-mode") :mode)
    (state/bar-section (str "{:x " x " :y " y "}") :cursor)
    (if (= mode :interpret)
      (state/bar-section (str "stack: " (str/join ", " (state/get-state :operation-stack))) :stack)
      (state/update-state :bar-status (dissoc (state/get-state :bar-status) :stack)))

    (render term @state)))

(defn run [term state]
  (t/in-terminal term
                 (interval #(tick term state) 100)
                 (editor-loop term @state)))

(defn open []
  (state/update-state :lines (-get-lines "resources/foo.txt"))
  (let [term (t/get-terminal :unix)]
    (t/add-resize-listener term (fn [x y] (dosync (handle-resize x y))))
    (run term state/state)))

(defn -main []
  (open))
