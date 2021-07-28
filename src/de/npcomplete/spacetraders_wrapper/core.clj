(ns de.npcomplete.spacetraders-wrapper.core
  (:require [de.npcomplete.spacetraders-wrapper.util :as u]
            [clojure.string :as str]))

(def ^:private base-url "https://api.spacetraders.io")

(defn build-url
  [& parts]
  (.toString ^StringBuilder
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

(defn account-info
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


(defn place-order!
  [token order-type ship-id good quantity]
  (if-not (#{:buy :sell} order-type)
    {:error {:message (str "Order type must be :buy or :sell. Was " (prn-str order-type)), :code -1}}
    (let [endpoint (if (= :buy order-type)
                     "/my/purchase-orders"
                     "/my/sell-orders")]
      (u/json-request {:method :post
                       :url (build-url endpoint)
                       :query-params {:token token}
                       :form-params {:shipId ship-id
                                     :good (str/upper-case (name good))
                                     :quantity quantity}}))))


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

(def available-loans
  (memoize
    (fn [token]
      (u/json-request {:method :get
                       :url (build-url "/types/loans")
                       :query-params {:token token}}))))


;; endpoints with /users/

(defn claim-username!
  [name]
  (u/json-request {:method :post
                   :url (build-url "/users/" (u/percent-encode name) "/claim")}))
