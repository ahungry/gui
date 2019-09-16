(ns ahungry.gui
  (:require
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
(native!)

;; (def normal-font "ARIAL-12-PLAIN")
;; (def normal-font "ARIAL-20-PLAIN")
(def normal-font "ARIAL-PLAIN-20")
(def title-font "ARIAL-14-BOLD")

(defn laf-selector []
  (horizontal-panel
   :items ["Substance skin: "
           (combobox
            :model    (vals (SubstanceCortex$GlobalScope/getAllSkins))
            :renderer (fn [this {:keys [value]}]
                        (text! this (.getClassName value)))
            :listen   [:selection (fn [e]
                                        ; Invoke later because CB doens't like changing L&F while
                                        ; it's doing stuff.
                                    (invoke-later
                                     (-> e
                                         selection
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
      (draw (polygon [0 h] [(/ w 4) 0] [(/ w 2) (/ h 2)] [w (/ h 2)] [0 h])
                     (style :foreground java.awt.Color/BLACK
                       :background (color 128 128 128 128)
                       :stroke     (stroke :width 4)))
      (.setColor (color 224 224 0 128))
      (.fillRect 0 0 (/ w 2) (/ h 2))
      (.setColor (color 0 224 224 128))
      (.fillRect 0 (/ h 2) (/ w 2) (/ h 2))
      (.setColor (color 224 0 224 128))
      (.fillRect (/ w 2) 0 (/ w 2) (/ h 2))
      (.setColor (color 224 0 0 128))
      (.fillRect (/ w 2) (/ h 2) (/ w 2) (/ h 2))
      (.setColor (color 0 0 0))
      (.drawString "Hello. This is a canvas example" 20 20))))

(def text-style (style :foreground (color 0 0 0)
                       :font "ARIAL-BOLD-24"))

(def star
  (path []
    (move-to 0 20) (line-to 5 5)
    (line-to 20 0) (line-to 5 -5)
    (line-to 0 -20) (line-to -5 -5)
    (line-to -20 0) (line-to -5 5)
    (line-to 0 20)))

(defn paint2 [c g]
  (let [w (.getWidth c)  w2 (/ w 2)
        h (.getHeight c) h2 (/ h 2)]
    (draw g
      (ellipse 0  0  w2 h2) (style :background (color 224 224 0 128))
      (ellipse 0  h2 w2 h2) (style :background (color 0 224 224 128))
      (ellipse w2 0  w2 h2) (style :background (color 224 0 224 128))
      (ellipse w2 h2 w2 h2) (style :background (color 224 0 0 128)))
    (push g
      (rotate g 20)
      (draw g (string-shape 20 20  "Hello. This is a canvas example") text-style))
    (push g
      (translate g w2 h2)
      (draw g star (style :foreground java.awt.Color/BLACK :background java.awt.Color/YELLOW)))))

; Create an action that swaps the paint handler for the canvas.
; Note that we can use (config!) to set the :paint handler just like
; properties on other widgets.
(defn switch-paint-action [n paint]
  (action :name n
          :handler #(-> (to-frame %)
                      (select [:#canvas])
                      (config! :paint paint))))
;; END paint sample from canvas.clj in seesaw repo

(defn make-canvas-panel []
  (canvas :id :canvas1
          :background "#BBBBDD"
          :paint paint1))

(defn make-canvas-panel2 []
  (canvas :id :canvas2
          :background "#BBBBDD"
          :paint paint2))

;; We can do a dynamic or REPL based flow with config!
;; (def x (make-canvas-panel))
;; (show x)
;; (config! x :paint 1)
;; (config! x :paint 2)

(defn make-tabbed-pane []
  (tabbed-panel :tabs [{:title "One" :content (make-canvas-panel)}
                       {:title "Two" :content (make-canvas-panel2)}]))

(defn a-test [e]
  (prn e)
  (alert "Hello"))

(defn make-menu []
  (let [a-test (action :handler a-test :name "Test" :tip "Pop up an alert" :key "menu A")]
    (menubar
     :items [(menu :text "File" :items [a-test])])))

(defn show
  "REPL friendly way to pop up what we're working on."
  [f]
  (invoke-later
   (->
    (frame
     :minimum-size [640 :by 480]
     :menubar (make-menu)
     :title "Widget"
     :content f)
    pack!
    show!)))

(defn make-switchable-canvas []
  (vertical-panel
   :items
   [(canvas :id :canvas :background "#BBBBDD" :paint paint1)
    (horizontal-panel :items ["Switch canvas paint function: "
                              (switch-paint-action "None" nil)
                              (switch-paint-action "Rectangles" paint1)
                              (switch-paint-action "Ovals" paint2)])]))

(defn make-laf-stuff []
  (border-panel
   :hgap 5 :vgap 5 :border 5
   :center (vertical-panel
            :items [
                    :separator
                    (laf-selector)
                    (text :multi-line? true :text notes :border 5 :font normal-font)
                    :separator
                    (label :text "A Label")
                    (button :text "A Button")
                    (checkbox :text "A checkbox")
                    (combobox :model ["A combobox" "more" "items"])
                    (horizontal-panel
                     :border "Some radio buttons"
                     :items (map (partial radio :text)
                                 ["First" "Second" "Third"]))
                    (scrollable (listbox :model (range 100)))])))

(defn make-main []
  (tabbed-panel
   :tabs
   [
    {:title "Look and Feel" :content (make-laf-stuff)}
    {:title "Switchable Canvas" :content (make-switchable-canvas)}
    {:title "Paint1" :content (make-canvas-panel)}
    {:title "Paint2" :content (make-canvas-panel2)}
    ]))

(defn draw-a-red-x
  "Draw a red X on a widget with the given graphics context"
  [c g]
  (let [w          (width c)
        h          (height c)
        line-style (style :foreground "#FF0000" :stroke 3 :cap :round)
        d 5]
    (draw g
      (line d d (- w d) (- h d)) line-style
      (line (- w d) d d (- h d)) line-style)))

(defn make-red-x []
  (flow-panel
    :border 5
    :items [
      (label  :text "I'm a good label!" :font "ARIAL-BOLD-40" :foreground "#00AA00")
      (label  :text "I'm a bad label!"  :font "ARIAL-BOLD-40" :paint draw-a-red-x)
      (button :text "I'm a bad button!"  :font "ARIAL-BOLD-40" :paint draw-a-red-x)]))

;; look in seesaw text_editor sample for a menu bar and probably key bindings

(defn -main [& args]
  (invoke-later
   (->
    (frame
     :title "Seesaw Substance/Insubstantial Example"
     :minimum-size [640 :by 480]
     :menubar (make-menu)
     :on-close :exit
     :content
     (make-main)
     )
    pack!
    show!)
   ;; Calling this, or setting it via REPL causes some issues...
   (SubstanceCortex$GlobalScope/setSkin "org.pushingpixels.substance.api.skin.NebulaSkin")
   ))

(log/debug "Fin")
