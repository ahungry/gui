(ns ahungry.keys-test
  (:require
   [clojure.test :refer :all]
   [ahungry.keys :refer :all]
   ))

(deftest code->key-test
  (testing "We can convert a java.awt.event.KeyEvent to a readable key kw."
    (is (= :shift (code->key 16)))
    (is (= :ctrl (code->key 17)))
    (is (= :meta (code->key 18)))
    (is (= :super (code->key 524)))
    (is (= nil (code->key -1)))))

(deftest modkey?-test
  (testing "Modkeys should show up as such."
    (is (not (modkey? :x)))
    (is (modkey? :shift))))

(deftest modchar->modkey-test
  (testing "We can convert strings to the proper kw."
    (is (= :ctrl (modchar->modkey "C")))
    (is (= :meta (modchar->modkey "M")))
    (is (= :super (modchar->modkey "S")))
    (is (= "x" (modchar->modkey "x")))))
