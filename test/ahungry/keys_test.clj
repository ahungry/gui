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
    (is (= \A (code->key 65)))))

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

(deftest keystring->keybind-test
  (testing "We can do the conversions."
    (is (= #{:ctrl :meta "f"} (keystring->keybind "C-M-f")))))

(deftest active-modkeys-test
  (testing "We can extract active keys."
    (is (= #{:ctrl :meta} (active-modkeys {:ctrl true :meta true :super nil})))))

(deftest is-keyequal?-test
  (testing "The equality comparisons hold up."
    (is (= true (is-keyequal? "m" \m {:ctrl false :meta false})))
    (is (= false (is-keyequal? "C-m" \m {:ctrl true :meta true})))
    (is (= true (is-keyequal? "C-M-b" \b {:ctrl true :meta true})))
    (is (= true (is-keyequal? "M-m" \m {:ctrl false :meta true})))))

(deftest dispatch-get-fns-test
  (testing "That functions or values we expect to return do so."
    (let [keymap {"a" 42
                  "C-b" 52
                  "C-M-x" 90}]
      (is (= 42 (first (dispatch-get-fns keymap \a {:ctrl nil}))))
      (is (= 52 (first (dispatch-get-fns keymap \b {:ctrl true}))))
      (is (= 90 (first (dispatch-get-fns keymap \x {:ctrl true :meta true}))))
      (is (= '() (dispatch-get-fns keymap \z {})))
      )
    ))
