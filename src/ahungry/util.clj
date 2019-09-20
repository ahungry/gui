(ns ahungry.util)

(defn contains-all?
  "Ensure that set 2 (s2) contains every member listed in set 1 (s1)."
  [x1 x2]
  (let [s1 (set x1)
        s2 (set x2)]
    (= (count s1) (count (filter #(contains? s2 %) s1)))))
