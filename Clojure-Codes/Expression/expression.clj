(defn operations [action]
  (fn [& operands]
    (fn [args] (apply action (map (fn [x] (x args)) operands)))))

(def constant constantly)
(defn variable [name] (fn [args] (args name)))
(defn min2 [x y] (if (< x y) x y))
(defn max2 [x y] (if (> x y) x y))

(def add (operations +))
(def subtract (operations -))
(def multiply (operations *))
(def divide (operations (fn [x & xs] (/ (double x) (double (apply * xs))))))
(def negate (operations -))
(def med (operations (fn [& xs] (nth (sort xs) (quot (count xs) 2)))))
(def avg (operations (fn [& xs] (/ (apply + xs) (count xs)))))
(def square (operations (fn [x] (* x x))))
(def sqrt (operations (fn [x] (Math/sqrt (Math/abs x)))))
(def min (operations (fn [& xs] (reduce (fn [res i] (min2 res i)) (first xs) xs))))
(def max (operations (fn [& xs] (reduce (fn [res i] (max2 res i)) (first xs) xs))))

(def ops {'+ add, '- subtract, '* multiply, '/ divide, 'negate negate, 'avg avg, 'med med, 'min min, 'max max, 'sqrt sqrt, 'square square})

(defn rec [clojureExpr]
  (cond
    (number? clojureExpr) (constant clojureExpr)
    (symbol? clojureExpr) (variable (str clojureExpr))
    (seq? clojureExpr) (apply (ops (first clojureExpr)) (map rec (rest clojureExpr)))))

(defn parseFunction [expression] (rec (read-string expression)))