(ns de.npcomplete.spacetraders-wrapper.core-with-token
  "This namespace is a convenience version of the core ns, where
  all functions which take the token as their first argument will instead
  take it from an environment variable."
  (:require [de.npcomplete.spacetraders-wrapper.core]))

(def ^:private token (delay (System/getenv "SPACE_TRADERS_TOKEN")))


(defn ^:private auth-type
  [fn-var]
  (-> (meta fn-var)
      :arglists
      ffirst
      (case
        'token :token-vars
        :basic-vars)))


(defn ^:private find-candidates
  []
  (->> (ns-publics 'de.npcomplete.spacetraders-wrapper.core)
       (map val)
       (filter (comp :arglists meta))                       ;; find functions
       (group-by auth-type)))


(defn ^:private add-arg-name
  "Adds a name symbol to the argument if it is a destructuring map or vector"
  [arg]
  (cond
    (and (map? arg) (not (:as arg)))
    (assoc arg :as (gensym "map-arg__"))
    (and (vector? arg) (not= :as (last (butlast arg))))
    (conj arg :as (gensym "vec-arg__"))
    :else arg))


(defn ^:private arg-name
  "Extracts the name-symbol from map or vector destructuring/vararg arguments"
  [arg]
  (cond
    (map? arg) (:as arg)
    (vector? arg) (last arg)
    :else arg))

(defn ^:private extract-args
  [args-vector]
  (let [[mandatory _ varargs] (partition-by #{'&} args-vector)]
    (conj
      (mapv arg-name mandatory)
      (or (arg-name (first varargs)) []))))


(defmacro ^:private def-public-functions
  []
  (let [{:keys [token-vars basic-vars]} (find-candidates)]
    `(do
       ;; add defs for all non token-taking functions
       ~@(for [basic-var basic-vars
               :let [{:keys [name doc]} (meta basic-var)]]
           `(def ~name ~(or doc "") @~basic-var))
       ;; add defs for all token-taking functions
       ~@(for [token-var token-vars
               :let [{:keys [name doc arglists]} (meta token-var)
                     args-vec (mapv add-arg-name (nfirst arglists))]]
           `(let [token-fn# @~token-var]
              (defn ~name
                ~(or doc "")
                ~args-vec
                (apply token-fn# @token ~@(extract-args args-vec))))))))


(def-public-functions)
