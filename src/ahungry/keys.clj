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

(def modkeys (atom {:ctrl nil
                     :meta nil
                     :shift nil
                     :super nil}))

(defn code->key
  "Receive a java.awt.event.KeyEvent key code N, turn it into the readable key."
  [n]
  (case n
    16 :shift
    17 :ctrl
    18 :meta
    524 :super
    nil))

(defn modkey? [kw] (contains? @modkeys kw))
(defn e->char [e] (.getKeyChar e))
(defn e->code [e] (.getKeyCode e))
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
  (->> (partition 1 m)
       (map first)
       (filter (fn [[_ v]] (= true v)))
       (map first)
       set))

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
     (= keybind-base (str c)))))

(defn handle-key-released
  "Unset key handling facilities."
  [e]
  (let [key (e->key e)]
    (when (modkey? key)
      (log/debug "Modkey is releaed, unsetting it.")
      (swap! modkeys assoc-in [key] false))))

(defn handle-key-pressed
  "Set key handling facilities."
  [e]
  (let [key (e->key e)]
    (log/debug "Key press event received: " e)
    (log/debug "Translated e->key to: " key)
    (when (modkey? key)
      (log/debug "Modkey is held down, setting it.")
      (swap! modkeys assoc-in [key] true))))
