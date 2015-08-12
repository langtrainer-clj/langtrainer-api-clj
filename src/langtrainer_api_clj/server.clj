(ns langtrainer-api-clj.server
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn parse-port [port]
  (when port
   (cond
     (string? port) (Integer/parseInt port)
     (number? port) port
     :else          (throw (Exception. (str "invalid port value: " port))))))

(defrecord App [service-map runnable-service routes]
  component/Lifecycle

  (start [this]
    (if runnable-service ; already started
      this
      (let [port (parse-port (:port service-map))]
        (println (str "\nStarting server on port: " port))
        (assoc this :runnable-service
          (run-jetty (:routing-table routes)
                     (merge service-map
                       {:port port
                        :join? false}))))))

  (stop [this]
    (if (not runnable-service) ; already stopped
      this
      (do (.stop runnable-service)
          (assoc this :runnable-service nil)))))

(defn new-server [service-map]
  (map->App {:service-map service-map}))
