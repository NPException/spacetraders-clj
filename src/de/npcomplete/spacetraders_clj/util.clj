(ns de.npcomplete.spacetraders-clj.util
  (:require [clojure.string :as str]
            [clojure.data.json :as json]
            [ring.util.codec :as codec]
            [org.httpkit.client :as http])
  (:import (javax.net.ssl SSLEngine SSLParameters SNIHostName)
           (java.net URI)))

;; copied from org.httpkit.sni-client/ssl-configurer and adjusted to work only with Java 11+

(defn ^:private ssl-configurer
  [^SSLEngine ssl-engine ^URI uri]
  (let [^SSLParameters ssl-params (.getSSLParameters ssl-engine)]
    (.setEndpointIdentificationAlgorithm ssl-params "HTTPS")
    (.setServerNames ssl-params [(SNIHostName. (.getHost uri))])
    (when (not (.getUseClientMode ssl-engine))
      (.setUseClientMode ssl-engine true))
    (doto ssl-engine
      (.setSSLParameters ssl-params))))

(defonce ^:private client
  (delay
    (org.httpkit.client/make-client
      {:ssl-configurer ssl-configurer})))


(defn http-request
  [request]
  ;; TODO: handle rate limiting
  @(http/request (assoc request :client (force client))))


(defn parse-json-body
  [http-response]
  (-> http-response
      :body
      (json/read-str :key-fn keyword)))


(defn json-request
  "Does a request which expects a json response"
  [request]
  (-> (http-request request)
      (parse-json-body)))


(defn percent-encode
  [s]
  (-> (codec/url-encode s)
      (str/replace "+" "%2B")))
