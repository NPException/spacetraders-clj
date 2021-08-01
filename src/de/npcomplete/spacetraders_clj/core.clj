(ns de.npcomplete.spacetraders-clj.core
  (:require [de.npcomplete.spacetraders-clj.web :as web]
            [clojure.string :as str]))

;; Compatible with Space Traders API version 1.0.0


;; helper functions

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


(defn ^:private ->location-symbol
  [x]
  (or (:symbol x) x))

(defn ^:private ->location-type
  [x]
  (or (:type x) x))

(defn ^:private ->ship-id
  [x]
  (or (:id x) x))

(defn ^:private ->ship-type
  [x]
  (or (:type x) x))

(defn ^:private ->ship-class
  [x]
  (or (:class x) x))

(defn ^:private ->flight-plan-id
  [x]
  (or (:id x) x))

(defn ^:private ->structure-id
  [x]
  (or (:id x) x))

(defn ^:private ->loan-id
  [x]
  (or (:id x) x))

(defn ^:private ->loan-type
  [x]
  (or (:type x) x))


;; endpoints with /game/

(defn game-status
  "Use to determine whether the server is alive"
  []
  (web/json-request {:method :get
                     :url (build-url "/game/status")}))


(defn leaderboard
  "Use to see the current net worth of the top players"
  [token]
  (web/json-request {:method :get
                     :url (build-url "/game/leaderboard/net-worth")
                     :query-params {:token token}}))


;; endpoints with /locations/

(defn location-info
  "Get info on a location"
  [token location]
  (web/json-request {:method :get
                     :url (build-url "/locations/" (->location-symbol location))
                     :query-params {:token token}}))


(defn location-marketplace
  "Get info on a location's marketplace"
  [token location]
  (web/json-request {:method :get
                     :url (build-url "/locations/" (->location-symbol location) "/marketplace")
                     :query-params {:token token}}))


(defn location-ships
  "Get the ships at a location"
  [token location]
  (web/json-request {:method :get
                     :url (build-url "/locations/" (->location-symbol location) "/ships")
                     :query-params {:token token}}))


(defn location-structures
  "Get the structures at a location"
  [token location]
  (web/json-request {:method :get
                     :url (build-url "/locations/" (->location-symbol location) "/structures")
                     :query-params {:token token}}))


;; endpoints with /my/

(defn my-account
  "Get information on your account"
  [token]
  (web/json-request {:method :get
                     :url (build-url "/my/account")
                     :query-params {:token token}}))


(defn create-flight-plan!
  "Submit a new flight plan"
  [token ship destination]
  (web/json-request {:method :post
                     :url (build-url "/my/flight-plans")
                     :query-params {:token token}
                     :form-params {:shipId (->ship-id ship)
                                   :destination (->location-symbol destination)}}))


(defn my-flight-plan-info
  "Get info on an existing flight plan"
  [token flight-plan]
  (web/json-request {:method :get
                     :url (build-url "/my/flight-plans/" (->flight-plan-id flight-plan))
                     :query-params {:token token}}))


(defn my-loans
  "Get your loans"
  [token]
  (web/json-request {:method :get
                     :url (build-url "/my/loans")
                     :query-params {:token token}}))


(defn take-out-loan!
  "Take out a loan"
  [token loan]
  (web/json-request {:method :post
                     :url (build-url "/my/loans")
                     :query-params {:token token}
                     :form-params {:type (str/upper-case (name (->loan-type loan)))}}))


(defn pay-off-loan!
  "Pay off your loan"
  [token loan]
  (web/json-request {:method :put
                     :url (build-url "/my/loans/" (->loan-id loan))
                     :query-params {:token token}}))


(defn place-purchase-order!
  "Place a new purchase order"
  [token ship good quantity]
  (web/json-request {:method :post
                     :url (build-url "/my/purchase-orders")
                     :query-params {:token token}
                     :form-params {:shipId (->ship-id ship)
                                   :good (str/upper-case (name good))
                                   :quantity quantity}}))


(defn place-sell-order!
  "Place a new sell order"
  [token ship good quantity]
  (web/json-request {:method :post
                     :url (build-url "/my/sell-orders")
                     :query-params {:token token}
                     :form-params {:shipId (->ship-id ship)
                                   :good (str/upper-case (name good))
                                   :quantity quantity}}))


(defn buy-ship!
  "Buy a new ship (takes either a ship type string, or known ship map)"
  [token location ship]
  (web/json-request {:method :post
                     :url (build-url "/my/ships")
                     :query-params {:token token}
                     :form-params {:location (->location-symbol location)
                                   :type (->ship-type ship)}}))


(defn sell-ship!
  "Scrap your ship for credits"
  [token ship]
  (web/json-request {:method :delete
                     :url (build-url "/my/ships/" (->ship-id ship))
                     :query-params {:token token}}))


(defn my-ships
  "Get your ships"
  [token]
  (web/json-request {:method :get
                     :url (build-url "/my/ships")
                     :query-params {:token token}}))


(defn my-ship-info
  "Get your ship info"
  [token ship]
  (web/json-request {:method :get
                     :url (build-url "/my/ships/" (->ship-id ship))
                     :query-params {:token token}}))


(defn jettison-cargo!
  "Jettison cargo"
  [token ship good quantity]
  (web/json-request {:method :post
                     :url (build-url "/my/ships/" (->ship-id ship) "/jettison")
                     :query-params {:token token}
                     :form-params {:good (str/upper-case (name good))
                                   :quantity quantity}}))


(defn transfer-cargo!
  "Transfer cargo between ships"
  [token from-ship to-ship good quantity]
  (web/json-request {:method :post
                     :url (build-url "/my/ships/" (->ship-id from-ship) "/transfer")
                     :query-params {:token token}
                     :form-params {:toShipId (->ship-id to-ship)
                                   :good (str/upper-case (name good))
                                   :quantity quantity}}))


(defn my-structures
  "Use to see all of your structures"
  [token]
  (web/json-request {:method :get
                     :url (build-url "/my/structures/")
                     :query-params {:token token}}))


(defn create-structure!
  "Create a new structure"
  [token location type]
  (web/json-request {:method :post
                     :url (build-url "/my/structures")
                     :query-params {:token token}
                     :form-params {:location (->location-symbol location)
                                   :type (str/upper-case (name type))}}))


(defn my-structure-info
  "Use to see a specific structure you own"
  [token structure]
  (web/json-request {:method :get
                     :url (build-url "/my/structures/" (->structure-id structure))
                     :query-params {:token token}}))


;; TODO: check if the "non-my" deposit function works for own structures too (aka: is this function redundant?)
(defn deposit-goods-to-owned-structure!
  "Deposit goods to a structure you own"
  [token structure ship good quantity]
  (web/json-request {:method :post
                     :url (build-url "/my/structures/" (->structure-id structure) "/deposit")
                     :query-params {:token token}
                     :form-params {:shipId (->ship-id ship)
                                   :good (str/upper-case (name good))
                                   :quantity quantity}}))


(defn take-goods!
  "Transfer goods from your structure to a ship"
  [token structure ship good quantity]
  (web/json-request {:method :post
                     :url (build-url "/my/structures/" (->structure-id structure) "/transfer")
                     :query-params {:token token}
                     :form-params {:shipId (->ship-id ship)
                                   :good (str/upper-case (name good))
                                   :quantity quantity}}))


(defn warp-jump!
  "Attempt a warp jump"
  [token ship]
  (web/json-request {:method :post
                     :url (build-url "/my/warp-jumps")
                     :query-params {:token token}
                     :form-params {:shipId (->ship-id ship)}}))


;; endpoints with /structures/

(defn structure-info
  "See specific structure (ship must be docked at same location?)"
  [token structure]
  (web/json-request {:method :get
                     :url (build-url "/structures/" (->structure-id structure))
                     :query-params {:token token}}))


(defn deposit-goods!
  "Deposit goods to a structure"
  [token structure ship good quantity]
  (web/json-request {:method :post
                     :url (build-url "/structures/" (->structure-id structure) "/deposit")
                     :query-params {:token token}
                     :form-params {:shipId (->ship-id ship)
                                   :good (str/upper-case (name good))
                                   :quantity quantity}}))


;; endpoints with /systems/

(defn system-info
  "Get systems info"
  [token system]
  (web/json-request {:method :get
                     :url (build-url "/systems/" (->location-symbol system))
                     :query-params {:token token}}))


(defn system-flight-plans
  "Get all active flight plans in the system"
  [token system]
  (web/json-request {:method :get
                     :url (build-url "/systems/" (->location-symbol system) "/flight-plans")
                     :query-params {:token token}}))


(defn system-locations
  "Get location info for a system"
  [token system & [type]]
  (web/json-request {:method :get
                     :url (build-url "/systems/" (->location-symbol system) "/locations")
                     :query-params {:token token
                                    :type (some-> type (->location-type) (name) (str/upper-case))}}))


(defn system-ship-listings
  "Get a list of all ships available to buy in the system"
  [token system & [class]]
  (web/json-request {:method :get
                     :url (build-url "/systems/" (->location-symbol system) "/ship-listings")
                     :query-params {:token token
                                    :class (->ship-class class)}}))


(defn system-docked-ships
  "Get info on a system's docked ships"
  [token system]
  (web/json-request {:method :get
                     :url (build-url "/systems/" (->location-symbol system) "/ships")
                     :query-params {:token token}}))


;; endpoints with /types/
;; note: the return values of these endpoints don't usually change, and can be memoized if desired.

(defn available-goods
  "Get available goods"
  [token]
  (web/json-request {:method :get
                     :url (build-url "/types/goods")
                     :query-params {:token token}}))


(defn available-loans
  "Get available loans"
  [token]
  (web/json-request {:method :get
                     :url (build-url "/types/loans")
                     :query-params {:token token}}))


(defn available-ships
  "Get info on available ships"
  [token]
  (web/json-request {:method :get
                     :url (build-url "/types/ships")
                     :query-params {:token token}}))


(defn available-structures
  "Get available structures"
  [token]
  (web/json-request {:method :get
                     :url (build-url "/types/structures")
                     :query-params {:token token}}))


;; endpoints with /users/

(defn claim-username!
  "Claim a username and get a token"
  [name]
  (web/json-request {:method :post
                     :url (build-url "/users/" (web/percent-encode name) "/claim")}))
