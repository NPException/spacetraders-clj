(ns de.npcomplete.spacetraders-wrapper.core
  (:require [de.npcomplete.spacetraders-wrapper.util :as u]
            [clojure.string :as str]))

(def ^:private base-url "https://api.spacetraders.io")

(defn ^:private build-url
  [& parts]
  (.toString
    ^StringBuilder
    (reduce
      (fn [^StringBuilder sb ^Object x]
        (.append sb (if (nil? x) "nil" (.toString x))))
      (StringBuilder. ^String base-url)
      parts)))

;; endpoints with /game/

(defn game-status
  []
  (u/json-request {:method :get
                   :url (build-url "/game/status")}))


;; endpoints with /locations/


;; endpoints with /my/

(defn my-account
  [token]
  (u/json-request {:method :get
                   :url (build-url "/my/account")
                   :query-params {:token token}}))


(defn take-out-loan!
  [token loan-type]
  (u/json-request {:method :post
                   :url (build-url "/my/loans")
                   :query-params {:token token}
                   :form-params {:type (str/upper-case (name loan-type))}}))


(defn ^:private place-order!
  [token order-type ship-id good quantity]
  (u/json-request {:method :post
                   :url (build-url (case order-type
                                     :buy "/my/purchase-orders"
                                     :sell "/my/sell-orders"))
                   :query-params {:token token}
                   :form-params {:shipId ship-id
                                 :good (str/upper-case (name good))
                                 :quantity quantity}}))

(defn buy-goods! [token ship-id good quantity]
  (place-order! token :buy ship-id good quantity))

(defn sell-goods! [token ship-id good quantity]
  (place-order! token :sell ship-id good quantity))


(defn buy-ship!
  [token location-id ship-type]
  (u/json-request {:method :post
                   :url (build-url "/my/ships")
                   :query-params {:token token}
                   :form-params {:location location-id
                                 :type ship-type}}))


;; endpoints with /systems/

(defn ship-listings
  [token system-id ship-class]
  (u/json-request {:method :get
                   :url (build-url "/systems/" system-id "/ship-listings")
                   :query-params {:token token
                                  :class ship-class}}))


;; endpoints with /types/
;; note: the return values of these endpoints don't usually change, and can be memoized if desired.

(defn available-loans
  [token]
  (u/json-request {:method :get
                   :url (build-url "/types/loans")
                   :query-params {:token token}}))


;; endpoints with /users/

(defn claim-username!
  [name]
  (u/json-request {:method :post
                   :url (build-url "/users/" (u/percent-encode name) "/claim")}))
