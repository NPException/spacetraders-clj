(ns de.npcomplete.spacetraders-clj.web
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


(defn ^:private wait-for-it!
  [response response-time]
  (let [sleep-time (-> response :headers :retry-after
                       (Double/parseDouble) (* 1000) (long)
                       ;; subtract estimated one-way ping
                       (- (quot response-time 2)))]
    (when (pos? sleep-time)
      (Thread/sleep sleep-time))))


(defn ^:private make-request!
  [request]
  (let [start (System/currentTimeMillis)
        response @(http/request request)
        time (- (System/currentTimeMillis) start)]
    [response time]))


(defn http-request
  [request]
  (let [request (assoc request :client (force client))]
    (locking http-request
      (loop [[response time] (make-request! request)]
        (if-not (= 429 (:status response))
          response
          (do (wait-for-it! response time)
              (recur (make-request! request))))))))


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
