(ns ahungry.gui-test
  (:require
   [clojure.test :refer :all]
   [clojure.tools.logging :as log]
   [ahungry.gui :refer :all]))

(deftest log-test
  (testing "The log call works and returns nil."
    (is (= nil (log/debug "Greetings from the test suite.
The file that controls log output is in:
  ./src/logback.xml

You can edit the verbosity level in there.")))))
