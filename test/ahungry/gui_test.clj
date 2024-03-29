(ns ahungry.gui-test
  (:require
   [clojure.test :refer :all]
   [clojure.tools.logging :as log]
   [ahungry.net :as net]
   [ahungry.gui :refer :all]
   [xdg-rc.core :as xdg-rc]
   ))

(deftest log-test
  (testing "The log call works and returns nil."
    (is (= nil (log/debug "Greetings from the test suite.
The file that controls log output is in:

  ./src/logback.xml

You can edit the verbosity level in there.")))))

(deftest net-test
  (testing "That we can pull some remote data with our chosen http libs."
    (let [res (net/get-json "https://httpbin.org/ip" {})]
      (println "Your IP was: ")
      (prn res)
      (is (= java.lang.String (type (:origin res)))))))

(deftest config-test
  (testing "That we can use this tool for this."
    (let [res (xdg-rc/get-xdg-config-home)]
      (println "Your home directory was...:")
      (prn res)
      (is (= java.lang.String (type res))))))
