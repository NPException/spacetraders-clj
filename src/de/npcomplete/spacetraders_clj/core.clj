(ns de.npcomplete.spacetraders-clj.core
  (:require [de.npcomplete.spacetraders-clj.util :as u]
            [clojure.string :as str]))

;; Compatible with Space Traders API version 1.0.0

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


(defn leaderboard
  "Use to see the current net worth of the top players"
  [token]
  (u/json-request {:method :get
                   :url (build-url "/game/leaderboard/net-worth")
                   :query-params {:token token}}))


;; endpoints with /locations/

(defn location-info
  [token location]
  (u/json-request {:method :get
                   :url (build-url "/locations/" location)
                   :query-params {:token token}}))


(defn location-marketplace
  [token location]
  (u/json-request {:method :get
                   :url (build-url "/locations/" location "/marketplace")
                   :query-params {:token token}}))


(defn location-ships
  [token location]
  (u/json-request {:method :get
                   :url (build-url "/locations/" location "/ships")
                   :query-params {:token token}}))


;; endpoints with /my/

(defn my-account
  [token]
  (u/json-request {:method :get
                   :url (build-url "/my/account")
                   :query-params {:token token}}))


(defn create-flight-plan!
  [token ship-id destination]
  (u/json-request {:method :post
                   :url (build-url "/my/flight-plans")
                   :query-params {:token token}
                   :form-params {:shipId ship-id
                                 :destination destination}}))


(defn my-flight-plan-info
  [token flight-plan-id]
  (u/json-request {:method :get
                   :url (build-url "/my/flight-plans/" flight-plan-id)
                   :query-params {:token token}}))


(defn my-loans
  "Get your loans"
  [token]
  (u/json-request {:method :get
                   :url (build-url "/my/loans")
                   :query-params {:token token}}))


(defn take-out-loan!
  "Take out a loan"
  [token loan-type]
  (u/json-request {:method :post
                   :url (build-url "/my/loans")
                   :query-params {:token token}
                   :form-params {:type (str/upper-case (name loan-type))}}))


(defn pay-off-loan!
  "Pay off your loan"
  [token loan-id]
  (u/json-request {:method :put
                   :url (build-url "/my/loans/" loan-id)
                   :query-params {:token token}}))


(defn place-purchase-order!
  "Place a new purchase order"
  [token ship-id good quantity]
  (u/json-request {:method :post
                   :url (build-url "/my/purchase-orders")
                   :query-params {:token token}
                   :form-params {:shipId ship-id
                                 :good (str/upper-case (name good))
                                 :quantity quantity}}))


(defn place-sell-order!
  "Place a new sell order"
  [token ship-id good quantity]
  (u/json-request {:method :post
                   :url (build-url "/my/sell-orders")
                   :query-params {:token token}
                   :form-params {:shipId ship-id
                                 :good (str/upper-case (name good))
                                 :quantity quantity}}))


(defn buy-ship!
  [token location ship-type]
  (u/json-request {:method :post
                   :url (build-url "/my/ships")
                   :query-params {:token token}
                   :form-params {:location location
                                 :type ship-type}}))


(defn my-ships
  [token]
  (u/json-request {:method :get
                   :url (build-url "/my/ships")
                   :query-params {:token token}}))


(defn my-ship-info
  [token ship-id]
  (u/json-request {:method :get
                   :url (build-url "/my/ships/" ship-id)
                   :query-params {:token token}}))


;; endpoints with /systems/

(defn system-info
  "Get systems info"
  [token system]
  (u/json-request {:method :get
                   :url (build-url "/systems/" system)
                   :query-params {:token token}}))


(defn system-flight-plans
  "Get all active flight plans in the system"
  [token system]
  (u/json-request {:method :get
                   :url (build-url "/systems/" system "/flight-plans")
                   :query-params {:token token}}))


(defn system-locations
  "Get location info for a system"
  [token system & [type]]
  (u/json-request {:method :get
                   :url (build-url "/systems/" system "/locations")
                   :query-params {:token token
                                  :type (some-> type (name) (str/upper-case))}}))


(defn system-ship-listings
  "Get a list of all ships available to buy in the system"
  [token system & [class]]
  (u/json-request {:method :get
                   :url (build-url "/systems/" system "/ship-listings")
                   :query-params {:token token
                                  :class class}}))


(defn system-docked-ships
  "Get info on a system's docked ships"
  [token system]
  (u/json-request {:method :get
                   :url (build-url "/systems/" system "/ships")
                   :query-params {:token token}}))


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
