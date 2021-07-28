(ns de.npcomplete.spacetraders-wrapper.core-with-token
  "This namespace is a convenience version of the core ns, where
  all functions which take the token as their first argument will instead
  take it from an environment variable.")

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
                     args (vec (nfirst arglists))]]
           `(let [token-fn# @~token-var]
              (defn ~name
                ~(or doc "")
                ~args
                (token-fn# @token ~@args)))))))


(def-public-functions)
