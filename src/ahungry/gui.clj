(ns ahungry.gui
  (:require
   [seesaw.core :as ss]
   [seesaw.keystroke :as sk]
   [seesaw.graphics :as ssg]
   [seesaw.color :as ssc]
   [seesaw.action :as ssa]
   [seesaw.config :as ssconfig]
   [clojure.tools.logging :as log]
   [ahungry.net :as net]
   )
  (:use
   [seesaw.core]
   [seesaw.graphics]
   [seesaw.color]
   )
  (:import org.pushingpixels.substance.api.SubstanceCortex$GlobalScope)
  (:gen-class))

;; TODO: Programatically set log level based on boot up or something.
;; :trace, :debug, :info, :warn, :error, :fatal
(log/debug "Boot")

;; Tries to match styles to native host some
(ss/native!)

;; (def normal-font "ARIAL-12-PLAIN")
;; (def normal-font "ARIAL-20-PLAIN")
(def normal-font "ARIAL-PLAIN-20")
(def title-font "ARIAL-14-BOLD")

(defn laf-selector []
  (ss/horizontal-panel
   :items ["Substance skin: "
           (ss/combobox
            :model    (vals (SubstanceCortex$GlobalScope/getAllSkins))
            :renderer (fn [this {:keys [value]}]
                        (ss/text! this (.getClassName value)))
            :listen   [:selection (fn [e]
                                        ; Invoke later because CB doens't like changing L&F while
                                        ; it's doing stuff.
                                    (ss/invoke-later
                                     (-> e
                                         ss/selection
                                         .getClassName
                                         SubstanceCortex$GlobalScope/setSkin)))])]))

(def notes " This example shows the available Substance skins. Substance
  is a set of improved look and feels for Swing. To use it in a project,
  you'll need to add a dep to your Leiningen project:

        [com.github.insubstantial/substance \"7.1\"]

  In this example, the full class name of the current skin is shown the
  in the combobox above. For your own apps you could either use a
  selector like this example, or, more likely, set a default initial
  skin in one of the following ways:

    Start your VM with -Dswing.defaultlaf=<class-name>

    Call (javax.swing.UIManager/setLookAndFeel \"<class-name>\")
    do this *after* (seesaw.core/native!) since that sets the L&F.

  See http://insubstantial.github.com/insubstantial/substance/docs/getting-started.html
  for more info. There you'll also find much more info about the
  skins along with much less crappy looking demos.")

;; BEGIN paint sample from canvas.clj in seesaw repo
(defn paint1 [c g]
  (let [w (.getWidth c)
        h (.getHeight c)]
    (doto g
      (ssg/draw (ssg/polygon [0 h] [(/ w 4) 0] [(/ w 2) (/ h 2)] [w (/ h 2)] [0 h])
                (ssg/style :foreground java.awt.Color/BLACK
                           :background (ssc/color 128 128 128 128)
                           :stroke     (ssg/stroke :width 4)))
      (.setColor (ssc/color 224 224 0 128))
      (.fillRect 0 0 (/ w 2) (/ h 2))
      (.setColor (ssc/color 0 224 224 128))
      (.fillRect 0 (/ h 2) (/ w 2) (/ h 2))
      (.setColor (ssc/color 224 0 224 128))
      (.fillRect (/ w 2) 0 (/ w 2) (/ h 2))
      (.setColor (ssc/color 224 0 0 128))
      (.fillRect (/ w 2) (/ h 2) (/ w 2) (/ h 2))
      (.setColor (ssc/color 0 0 0))
      (.drawString "Hello. This is a canvas example" 20 20))))

(def text-style (ssg/style :foreground (ssc/color 0 0 0)
                           :font "ARIAL-BOLD-24"))

;; These calls have to be unqualified because the macro is just converting
;; the symbols into method invocations of the thing being painted.
(def star
  (ssg/path []
            (move-to 0 20) (line-to 5 5)
            (line-to 20 0) (line-to 5 -5)
            (line-to 0 -20) (line-to -5 -5)
            (line-to -20 0) (line-to -5 5)
            (line-to 0 20)))

(def grow (atom {:x 0 :y 0 :f inc}))

(defn get-grow []
  (let [{:keys [x y f]} @grow]
    (when (> x 300)
      (swap! grow assoc-in [:f] dec))
    (when (< x 1)
      (swap! grow assoc-in [:f] inc))
    (swap! grow conj {:x (f x) :y (f y)})
    @grow))

(defn paint2 [c g]
  (let [grow (get-grow)
        ;; w (.getWidth c)  w2 (/ w 2)
        ;; h (.getHeight c) h2 (/ h 2)
        w2 (:x grow)
        h2 (:y grow)
        ]
    (ssg/draw g
              (ssg/ellipse 0  0  w2 h2) (ssg/style :background (ssc/color 224 224 0 128))
              (ssg/ellipse 0  h2 w2 h2) (ssg/style :background (ssc/color 0 224 224 128))
              (ssg/ellipse w2 0  w2 h2) (ssg/style :background (ssc/color 224 0 224 128))
              (ssg/ellipse w2 h2 w2 h2) (ssg/style :background (ssc/color 224 0 0 128)))
    (ssg/push g
              (ssg/rotate g 20)
              (ssg/draw g (ssg/string-shape 20 20  "Hello. This is a canvas example") text-style))
    (ssg/push g
              (ssg/translate g w2 h2)
              (ssg/draw g star (ssg/style :foreground java.awt.Color/BLACK :background java.awt.Color/YELLOW)))))

                                        ; Create an action that swaps the paint handler for the canvas.
                                        ; Note that we can use (config!) to set the :paint handler just like
                                        ; properties on other widgets.
(defn switch-paint-action [n paint]
  (ssa/action :name n
              :handler #(-> (ss/to-frame %)
                            (ss/select [:#canvas])
                            (ssconfig/config! :paint paint))))
;; END paint sample from canvas.clj in seesaw repo

(defn make-canvas-panel []
  (ss/canvas :id :canvas1
             :background "#BBBBDD"
             :paint paint1))

(defn make-canvas-panel2 []
  (ss/canvas :id :canvas2
             :background "#BBBBDD"
             :paint paint2))

;; We can do a dynamic or REPL based flow with config!
;; (def x (make-canvas-panel2))
;; (show x)
;; (ss/config! x :paint 1)
;; (ss/config! x :paint 2)
;; (while true (do (Thread/sleep 10) (ss/config! x :paint paint2)))

(defn make-tabbed-pane []
  (ss/tabbed-panel :tabs [{:title "One" :content (make-canvas-panel)}
                          {:title "Two" :content (make-canvas-panel2)}]))

(defn a-test [e]
  (prn e)
  (ss/alert "Hello"))

(defn make-menu []
  (let [a-test (ssa/action :handler a-test :name "Test" :tip "Pop up an alert" :key "menu A")]
    (ss/menubar
     :items [(ss/menu :text "File" :items [a-test])])))

(defn x-action-handler [e]
  (prn e))

(def x-action (action :name "XA" :key "alt 2" :handler x-action-handler))

(defn add-behaviors [root]
  (ss/config! (select root [:.xa]) :action x-action)
  root)

(defn make-switchable-canvas []
  (ss/vertical-panel
   :class :xa
   :items
   [(ss/canvas :id :canvas :background "#BBBBDD" :paint paint1)
    (ss/horizontal-panel :items ["Switch canvas paint function: "
                                 (switch-paint-action "None" nil)
                                 (switch-paint-action "Rectangles" paint1)
                                 (switch-paint-action "Ovals" paint2)])]))

(defn make-laf-stuff []
  (ss/border-panel
   ;; :class :xa
   :hgap 5 :vgap 5 :border 5
   :center (ss/vertical-panel
            :items [
                    :separator
                    (laf-selector)
                    (ss/text :multi-line? true :text notes :border 5 :font normal-font)
                    :separator
                    (ss/label :text "A Label")
                    (ss/button :text "A Button")
                    (ss/checkbox :text "A checkbox")
                    (ss/combobox :model ["A combobox" "more" "items"])
                    (ss/horizontal-panel
                     :border "Some radio buttons"
                     :items (map (partial ss/radio :text)
                                 ["First" "Second" "Third"]))
                    (ss/scrollable (ss/listbox :model (range 100)))])))

(defn make-main []
  (ss/tabbed-panel
   :tabs
   [
    {:title "Look and Feel" :content (make-laf-stuff)}
    {:title "Switchable Canvas" :content (make-switchable-canvas)}
    {:title "Paint1" :content (make-canvas-panel)}
    {:title "Paint2"
     ;; :icon (slurp "close-icon.png")
     :content (make-canvas-panel2)}
    ]))

;; (ss/listen x :key-pressed (fn [e] (prn e)))
;; Programatically interact with tabs via:
;; (select x) ; Get the selection
;; (select! x 0) ; Set the selection

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

(defn handle-key-released
  "Unset key handling facilities."
  [e]
  (let [key (e->key e)]
    (when (modkey? key)
      (swap! modkeys assoc-in [key] false))))

(defn handle-key-pressed
  "Set key handling facilities."
  [e]
  (let [key (e->key e)]
    (prn e)
    (prn key)
    (when (modkey? key)
      (swap! modkeys assoc-in [key] true))
    (prn @modkeys)))

(defn set-listeners! [x]
  (ss/listen x :key-released handle-key-released)
  (ss/listen x :key-pressed handle-key-pressed))


(defn modchar->modkey [s]
  (case s
    "C" :ctrl
    "M" :meta
    "S" :super
    s))

(defn keystring->keybind
  "Turns something like M-C-f into #{:ctrl :meta \f}."
  [s]
  (->> (clojure.string/split s #"-")
       (map modchar->modkey)
       set))

(defn active-modkeys
  "Just pull out the active modkeys."
  [m]
  (->> (partition 1 m)
       (map first)
       (filter (fn [[_ v]] (= true v)))
       (map first)
       set))

(defn contains-all?
  "Ensure that set 2 (s2) contains every member listed in set 1 (s1)."
  [x1 x2]
  (let [s1 (set x1)
        s2 (set x2)]
    (= (count s1) (count (filter #(contains? s2 %) s1)))))

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
        all-mods-match? (contains-all? keybind-mods active-modkeys)]
    (prn all-mods-match?)
    (and
     (= (count active-modkeys) (count keybind-mods))
     all-mods-match?
     (= keybind-base (str c)))))

(is-keyequal? "m" \m {:ctrl false :meta false}) ; true
(is-keyequal? "C-m" \m {:ctrl true :meta true}) ; false
(is-keyequal? "M-C-m" \m {:ctrl true :meta true}) ; true
(is-keyequal? "M-m" \m {:ctrl false :meta true}) ; true

(defn draw-a-red-x
  "Draw a red X on a widget with the given graphics context"
  [c g]
  (let [w          (ss/width c)
        h          (ss/height c)
        line-style (ssg/style :foreground "#FF0000" :stroke 3 :cap :round)
        d 5]
    (ssg/draw g
              (ssg/line d d (- w d) (- h d)) line-style
              (ssg/line (- w d) d d (- h d)) line-style)))

(defn make-red-x []
  (ss/flow-panel
   :border 5
   :items [
           (ss/label  :text "I'm a good label!" :font "ARIAL-BOLD-40" :foreground "#00AA00")
           (ss/label  :text "I'm a bad label!"  :font "ARIAL-BOLD-40" :paint draw-a-red-x)
           (ss/button :text "I'm a bad button!"  :font "ARIAL-BOLD-40" :paint draw-a-red-x)]))

;; look in seesaw text_editor sample for a menu bar and probably key bindings
(defn show
  "REPL friendly way to pop up what we're working on."
  [f]
  (ss/invoke-later
   (->
    (ss/frame
     :minimum-size [640 :by 480]
     :menubar (make-menu)
     :title "Widget"
     :content f)
    ;; add-behaviors
    ss/pack!
    ss/show!)
   (set-listeners! f)))

(def x (make-main))

(defn -main [& args]
  (ss/invoke-later
   (->
    (ss/frame
     :title "Seesaw Substance/Insubstantial Example"
     :minimum-size [640 :by 480]
     :menubar (make-menu)
     :on-close :exit
     :content
     (make-main)
     )
    ss/pack!
    ss/show!)
   ;; Calling this, or setting it via REPL causes some issues...
   (SubstanceCortex$GlobalScope/setSkin "org.pushingpixels.substance.api.skin.NebulaSkin")
   ))

(log/debug "Fin")
