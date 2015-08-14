(ns langtrainer-api-clj.routes
  (:use     [ring.middleware.resource])
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [routes]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [langtrainer-api-clj.handlers.world :refer [new-world-routes]]))

(defrecord Routes [routing-table world]
  component/Lifecycle

  (start [this]
    (if routing-table ; already started
      this
      (assoc this :routing-table
             (-> (routes
                   (new-world-routes world)
                   (route/not-found "Not Found"))
                 (wrap-json-response)
                 (wrap-defaults api-defaults)))))

  (stop [this]
    (if (not routing-table) ; already stopped
      this
      (assoc this :routing-table nil))))

(defn new-routes []
  (map->Routes {}))
