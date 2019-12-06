(defn scalar? [s] (number? s))
(defn scalars? [& ss] (every? scalar? ss))
(defn is_vector? [v] (and (vector? v) (apply scalars? v)))
(defn vectors? [& vs] (every? is_vector? vs))
(defn have_eq_size [f]
  (fn [& obs]
    (reduce
      (fn [res x] (and res (f (nth obs x) (nth obs (inc x)))))
      true (range (dec (count obs))))))
(def vectors_have_eq_sizes
  (have_eq_size
    (fn [v1 v2] (== (count v1) (count v2)))))
(defn matrix? [m] (and (vector? m) (apply vectors? m) (apply vectors_have_eq_sizes m)))
(defn matrices? [& ms] (every? matrix? ms))
(def matrices_have_eq_size
  (have_eq_size
    (fn [m1 m2] (and (== (count m1) (count m2)) (== (count (nth m1 0)) (count (nth m2 0)))))))
(defn matrices_can_be_mul [& ms]
  (reduce
    (fn [res x] (and res ((fn [m1 m2] (== (count m2) (count (nth m1 0)))) (nth ms x) (nth ms (inc x)))))
    true (range (dec (count ms)))))

(defn abstract_scalar_mul [action pre_cond]
  (fn [v & ss]
    {:pre [(pre_cond v) (apply scalars? ss)]}
    (mapv (fn [elem] (action elem (apply * ss))) v)))

(def v*s (abstract_scalar_mul * is_vector?))
(def m*s (abstract_scalar_mul v*s matrix?))

(defn abstract_operations [action1 other_action pre_cond]
  (fn [& operands]
    {:pre [(apply pre_cond operands)]}
    (if (== (count operands) 1)
      (action1 (nth operands 0))
      (reduce (fn [first second] (mapv other_action first second)) operands))))
(def v+ (abstract_operations identity + (and vectors? vectors_have_eq_sizes)))
(def v- (abstract_operations (fn [v] (v*s v -1)) - (and vectors? vectors_have_eq_sizes)))
(def v* (abstract_operations identity * (and vectors? vectors_have_eq_sizes)))
(def m+ (abstract_operations identity v+ (and matrices? matrices_have_eq_size)))
(def m- (abstract_operations (fn [v] (m*s v -1)) v- (and matrices? matrices_have_eq_size)))
(def m* (abstract_operations identity v* (and matrices? matrices_have_eq_size)))

(defn scalar [v1 v2] {:pre [(vectors? v1 v2) (vectors_have_eq_sizes v1 v2)]} (apply + (v* v1 v2)))

(defn bin_vect [v1 v2] {:pre [(vectors? v1 v2) (vectors_have_eq_sizes v1 v2) (== (count v1) 3)]}
  [(- (* (v1 1) (v2 2)) (* (v1 2) (v2 1))),
   (- (* (v1 2) (v2 0)) (* (v1 0) (v2 2))),
   (- (* (v1 0) (v2 1)) (* (v1 1) (v2 0)))])

(defn vect [& vs] (reduce bin_vect vs))

(defn m*v [m v] {:pre [(is_vector? v) (matrix? m)]} (mapv (fn [row] (scalar row v)) m))

(defn transpose [m] {:pre [matrix? m]} (apply mapv vector m))

(defn bin_m*m [m1 m2] {:pre [(matrix? m1) (matrix? m2) (matrices_can_be_mul m1 m2)]}
  (mapv (fn [v] (m*v (transpose m2) v)) m1))

(defn m*m [& ms] (reduce bin_m*m ms))