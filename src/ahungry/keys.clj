;; Facilities for Emacs-like key handling.

(ns ahungry.keys
  (:require
   [clojure.tools.logging :as log]
   [ahungry.util :as util]
   ))

;; Maps from Swing
;; key-pressed key-released key-typed
;; 16 - shift
;; 18 - alt
;; 17 - ctrl
;; 27 - esc
;; 524 - super left
;; 38 - ARROW_UP
;; 37 - ARROW_LEFT (by default moves through tab panes)
;; 39 - ARROW_RIGHT
;; 40 - ARROW_DOWN

(def *modkeys (atom {:ctrl nil
                     :meta nil
                     :shift nil
                     :super nil}))

(defn init!
  "Resets atom state."
  []
  (reset! *modkeys {:ctrl nil :meta nil :shift nil :super nil}))

(defn code->key
  "Receive a java.awt.event.KeyEvent key code N, turn it into the readable key."
  [n]
  (case n
    16 :shift
    17 :ctrl
    18 :meta
    524 :super
    9 :tab
    32 :space
    (char n)))

(defn modkey? [kw] (contains? @*modkeys kw))
(defn e->char [e] (.getKeyChar e))
(defn e->code [e]
  ;; (log/debug "Translating event to code char: " (.getKeyCode e))
  (.getKeyCode e))
(def e->key (comp code->key e->code))

(defn modchar->modkey [s]
  (case s
    "C" :ctrl
    "M" :meta
    "S" :super
    s))

(defn keystring->keybind
  "Turns something like C-M-f into #{:ctrl :meta \"f\"}."
  [s]
  (->> (clojure.string/split s #"-")
       (map modchar->modkey)
       set))

(defn active-modkeys
  "Just pull out/filter the active modkeys and return a set of them."
  [m]
  (->> (into [] m)
       (filter (fn [[_ v]] (= true v)))
       (map first)
       set))

(defn case-insensitive-eq
  "Compare strings S1 and S2 in a case insensitive manner."
  [s1 s2]
  (= (.toLowerCase s1) (.toLowerCase s2)))

;; TODO: Probably split keys by spaces to allow nested keys.
(defn is-keyequal?
  "Given an Emacs like key binding S (such as C-f (ctrl + f) or M-1 (meta
  + 1)), see if it is equal to the code C, while considering the
  associated modkeys states M."
  [s c m]
  (let [keybind (keystring->keybind s)
        keybind-base (first (filter (complement modkey?) keybind))
        keybind-mods (filter modkey? keybind)
        active-modkeys (active-modkeys m)
        all-mods-match? (util/contains-all? keybind-mods active-modkeys)]
    (and
     (= (count active-modkeys) (count keybind-mods))
     all-mods-match?
     (case-insensitive-eq keybind-base (str c)))))

(defn handle-key-released
  "Unset key handling facilities."
  [e]
  (let [key (e->key e)]
    (when (modkey? key)
      ;; (log/debug "Modkey is released, unsetting it.")
      (swap! *modkeys assoc-in [key] false))))

(defn foo [] (log/info "Foo was called."))
(defn bar [] (log/info "Bar was called."))

(def global-keymap
  {"a" #'foo
   "C-b" #'bar})

(defn filter-by-is-keyequal?
  "Expects col to be a col of tuples, where the first is the binding."
  [c state]
  (fn [col]
    (let [keybind (first col)]
      (is-keyequal? keybind c state))))

(defn dispatch-get-fns
  "Receives a map M of keybinds, a char C, and works against active STATE to find
  which keybind is appropriate to return a function for."
  [m c state]
  (->> (into [] m)
       (filter (filter-by-is-keyequal? c state))
       (map second)
       doall))

(defn dispatch
  "Receives a map M of keybinds, a char C, and works against active STATE to find
  which keybind is appropriate to return a function for and calls them."
  [m c state]
  (let [fs (dispatch-get-fns m c state)]
    ;; (log/debug "Dispatch came up with this list of functions to run: " fs)
    fs))

(defn handle-key-pressed
  "Set key handling facilities.  Receives a map M, which contains binds
  of translated keys to functions to run - returns a function to
  receive events as such."
  [m]
  (fn [e]
    (let [key (e->key e)]
      ;; (log/debug "Key press event received: " e)
      (log/debug "Translated e->key to: " key)
      (when (modkey? key)
        (log/debug "Modkey is held down, setting it.")
        (swap! *modkeys assoc-in [key] true))
      (->> (dispatch m key @*modkeys)
           (map #(%))
           doall))))
