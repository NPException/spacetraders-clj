(ns de.npcomplete.spacetraders-wrapper.core
  (:require [de.npcomplete.spacetraders-wrapper.util :as u]))

(def ^:private base-url "https://api.spacetraders.io")


(defn game-status
  []
  (-> (u/http-request {:method :get
                       :url (str base-url "/game/status")})
      (u/parse-json-body)))


(defn claim-username!
  [name]
  (-> (u/http-request {:method :post
                       :url (str base-url "/users/" (u/percent-encode name) "/claim")})
      (u/parse-json-body)))


(defn account-info
  [token]
  (-> (u/http-request {:method :get
                       :url (str base-url "/my/account")
                       :query-params {:token token}})
      (u/parse-json-body)))
