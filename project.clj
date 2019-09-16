(defproject ahungry/gui "0.1.0-SNAPSHOT"
  :description "GUI packages chosen to support a wide range of JRE environments."
  :url "https://github.com/ahungry/gui"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [seesaw "1.5.0"]

                 ;; Latest versions, but only work with java 9+
                 ;; [org.pushing-pixels/radiance-substance "2.5.1"]
                 [org.pushing-pixels/radiance-substance "1.0.2"]

                 ;; Logging related
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [org.clojure/tools.logging "0.5.0"]

                 ;; Network related
                 [clj-http "3.10.0"]
                 [cheshire "5.9.0"]

                 ;; Config or persistence related
                 [ahungry/xdg-rc "0.0.4"]

                 ]
  :main ahungry.gui
  :repl-options {:init-ns ahungry.gui}
  :java-source-paths ["java-src"]
  :aot [ahungry.gui]
  :target-path "target/%s"
  :profiles
  {
   ;; Required to use clojure spec check facilities.
   :dev {:dependencies [[org.clojure/test.check "0.9.0"]]}
   :uberjar {:aot :all}
   }
  :jvm-opts ["-Dfile.encoding=UTF8"
             "-Dswing.aatext=true"
             "-Dawt.useSystemAAFontSettings=lcd"
             ;; "-Dswing.defaultlaf=org.pushingpixels.substance.api.skin.DustSkin"
             ]
  :pom-addition [:properties
                 ["maven.compiler.source" "1.8"]
                 ["maven.compiler.target" "1.8"]])
