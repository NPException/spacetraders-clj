(ns de.npcomplete.spacetraders-wrapper.core
  (:require [de.npcomplete.spacetraders-wrapper.util :as u]
            [clojure.string :as str]))

(def ^:private base-url "https://api.spacetraders.io")

;; endpoints with /game/

(defn game-status
  []
  (-> (u/http-request {:method :get
                       :url (str base-url "/game/status")})
      (u/parse-json-body)))


;; endpoints with /my/

(defn account-info
  [token]
  (-> (u/http-request {:method :get
                       :url (str base-url "/my/account")
                       :query-params {:token token}})
      (u/parse-json-body)))


(defn take-out-loan!
  [token loan-type]
  (-> (u/http-request {:method :post
                       :url (str base-url "/my/loans")
                       :query-params {:token token}
                       :form-params {:type (str/upper-case (name loan-type))}})
      (u/parse-json-body)))


(defn place-order!
  [token order-type ship-id good quantity]
  (if-not (#{:buy :sell} order-type)
    {:error {:message (str "Order type must be :buy or :sell. Was " (prn-str order-type)), :code -1}}
    (let [endpoint (if (= :buy order-type)
                     "/my/purchase-orders"
                     "/my/sell-orders")]
      (-> (u/http-request {:method :post
                           :url (str base-url endpoint)
                           :query-params {:token token}
                           :form-params {:shipId ship-id
                                         :good (str/upper-case (name good))
                                         :quantity quantity}})
          (u/parse-json-body)))))


(defn buy-ship!
  [token location-id ship-type]
  (-> (u/http-request {:method :post
                       :url (str base-url "/my/ships")
                       :query-params {:token token}
                       :form-params {:location location-id
                                     :type ship-type}})
      (u/parse-json-body)))


;; endpoints with /systems/

(defn ship-listings
  [token system-id ship-class]
  (-> (u/http-request {:method :get
                       :url (str base-url "/systems/" system-id "/ship-listings")
                       :query-params {:token token
                                      :class ship-class}})
      (u/parse-json-body)))


;; endpoints with /types/

(def available-loans
  (memoize
    (fn [token]
      (-> (u/http-request {:method :get
                           :url (str base-url "/types/loans")
                           :query-params {:token token}})
          (u/parse-json-body)))))


;; endpoints with /users/

(defn claim-username!
  [name]
  (-> (u/http-request {:method :post
                       :url (str base-url "/users/" (u/percent-encode name) "/claim")})
      (u/parse-json-body)))
